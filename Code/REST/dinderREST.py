from flask import Flask, request, Response, abort,jsonify,request,make_response
import jwt #jsonwebToken #v1.7.1. bei aktueller Version Fehler beim Wrapper decode: It is required that you pass in a value for the \"algorithms\" argument when calling decode().
import datetime
from flask.helpers import send_file, send_from_directory
from mysql.connector import Error
import mysql.connector
import os 
from flask_swagger_ui import get_swaggerui_blueprint
from werkzeug.exceptions import BadRequest
from functools import wraps
from  pathlib import Path
import pathlib
from flask_cors import CORS, cross_origin



#TODO
# Maybe delete "," of tuple with multiple entries?
#Create account/profilfolder for pics
#MErke ein User darf nicht zwei Profile haben die gleich heißen!!
dbLoginInfo = {
    'host' : 'localhost',
    'port': '3306',
    'user': 'restAPI',
    'password' : '4537842',
    'database' : 'dindersql'
}

UPLOAD_FOLDER = 'rest/pics/'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg', 'gif'])

app = Flask(__name__)
app.config['SECRET_KEY'] = 'hgbkgjbkhvbjhv'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
CORS(app)


def decodeToken(token):
    if not token:
        return jsonify({'msg': 'Token not found'})
    try: 
        return jwt.decode(token,app.config['SECRET_KEY'])
    except Exception as e:
        print(e)
        return jsonify({'message' : str(e)}), 403



def token_required(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        token = request.args.get('token')
        if not token:
            return jsonify({'msg': 'Token not found'})
        try: 
           
            data = jwt.decode(token,app.config['SECRET_KEY'])
       
        except Exception as e:
            print(e)
            return jsonify({'message' : str(e)}), 403

        return f(*args, **kwargs)
    return decorated

#db will be edited = true
def exeQuery(query, data, dbEdit ):
    try:
        myDatabase = mysql.connector.connect(**dbLoginInfo)
        cursor = myDatabase.cursor()
    except mysql.connector.Error as e:
        print('[ERROR WHILE CONNECTING TO DATABASE]: ', e)
    else:
        if dbEdit == True:
            if data == None:
                res = cursor.execute(query)
                myDatabase.commit()
            else:
                res = cursor.execute(query, data)
                myDatabase.commit()
        else:
            if data == None:
                cursor.execute(query)
                res = cursor.fetchall()
                
            else: 
                cursor.execute(query, data)
                res = cursor.fetchall()
                
   
    cursor.close()
    myDatabase.close()
    return  res

@app.route("/test")
def testMethod():
    return str(exeQuery("SELECT * from konto", None, False))

def callStoredProc(procName, data):
    try:
        myDatabase = mysql.connector.connect(**dbLoginInfo)
        cursor = myDatabase.cursor()
    except mysql.connector.Error as e:
        print('[ERROR WHILE CONNECTING TO DATABASE]: ', e)
    else:
        proc = cursor.callproc(procName, data)
        myDatabase.commit()
        cursor.close()
        myDatabase.close()
        

def callSoredProcReturn(procName, data):
    try:
        myDatabase = mysql.connector.connect(**dbLoginInfo)
        cursor = myDatabase.cursor()
    except mysql.connector.Error as e:
        print('[ERROR WHILE CONNECTING TO DATABASE]: ', e)
    else:
    
        reVal = cursor.callproc(procName, data)
        #reVal = cursor.fetchone()
       
        myDatabase.commit()
        cursor.close()
        myDatabase.close()
       
        return reVal

@app.route("/account/new", methods= ['POST'])
def newAccount():
    #response 
    email = request.form.get('email')
    firstname = request.form.get('firstname')
    lastname = request.form.get('lastname')
    phoneNum = request.form.get('phoneNum')
    institution = request.form.get('institution')
    birthday = request.form.get('birthday')
    passwort = request.form.get('passwort')
    

    isOnline = 0
    inactiv = 0
    isBanned = 0
    try:
        callStoredProc('create_account', (email,lastname,firstname, phoneNum,birthday, passwort, institution,isOnline, inactiv, isBanned))

    except mysql.connector.Error as e:  
        print(e) 

        return abort(400, '[ERROR]: '+str(e))
       
    else:
        return Response(status = 200)

#untested
#when email changed what about the key
@app.route("/account/update", methods= ['POST'])
@token_required
def accUpdate():
    oldEmail = request.form.get('oldEmail')
    email = request.form.get('email')
    firstname = request.form.get('firstname')
    lastname = request.form.get('lastname')
    phoneNum = request.form.get('phoneNum')
    institution = request.form.get('institution')
    birthday = request.form.get('birthday')
    passwort = request.form.get('passwort')
    istOnline = request.form.get('istOnline')
    inaktiv = request.form.get('inaktiv')
    istGesperrt = request.form.get('istGesperrt')
    standort = request.form.get('standort')

    try:
        query = 'UPDATE konto SET email = %s, firstname = %s,lastname = %s,phoneNum = %s,institution = %s, birthday = %s,passwort = %s,istOnline = %s,inaktiv = %s,istGesperrt =%s,standort =%s WHERE email = %s';
        data = exeQuery(query, (email, firstname,lastname,phoneNum,institution ,birthday,passwort, istOnline, inaktiv ,istGesperrt, standort, oldEmail,), True)
    except mysql.connector.Error as e: 
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else: 
        return  Response(status = 200)

@app.route('/login')
def login():
    auth = request.authorization
        
    if auth and auth.password and auth.username:
        try:
            data = exeQuery('SELECT EXISTS (SELECT 1 FROM konto WHERE email = %s AND passwort = %s)', (  auth.username,   auth.password), False)
        
        except mysql.connector.Error as e:  
            print(e) 
            return abort(400, '[ERROR]: '+str(e))
        else:
            if data[0][0] == 1:
                token = jwt.encode({'user':  auth.username, 'exp': datetime.datetime.utcnow()+ datetime.timedelta(minutes = 120)}, app.config['SECRET_KEY'])
            
                #print(token)
                #jsonify({'token': token.decode('UTF-8')})
                return jsonify({'token':  token.decode('UTF-8')})
            else:
                return abort(400, '[ERROR]: Wrong email or passwort') 
    
    else:
        return  abort(400, '[ERROR]: One of the vars or both has no value')

@app.route('/getGps')
@token_required
def getGPS():
    
    dicUser = decodeToken(request.args.get('token'))
    
    try:
        #print(dicUser['user'])
        data = exeQuery('SELECT standort FROM konto WHERE email = %s ', (dicUser['user'],), False)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else: 
        return  jsonify({'gps' : data})

   # return  jsonify({'message' : 'This is only available for people with valid tokens.'})

@app.route('/setGps', methods= ['POST'])
@token_required
def setGPS():
    gps = str(request.form.get('gps'))
    dicUser = decodeToken(request.args.get('token')) 
    print(dicUser)
    try:
        print(dicUser['user'])
        data = exeQuery('UPDATE konto SET standort = %s WHERE email = %s ', (gps,dicUser['user'],), True)
    except mysql.connector.Error as e: 
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else: 
        return  Response(status = 200)

#untested
@app.route("/onlineOfflineStatus", methods= ['GET'])
@token_required
def status():
    dicUser = decodeToken(request.args.get('token'))
    
    try:
        data = exeQuery('SELECT istOnline FROM konto WHERE email = %s ', (dicUser['user'],), False)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else: 
        #data is 1 or 0
        return  jsonify({'istOnline' : data})




"""
{
 "profilInfo": 
    {
      "profilName": "Doe",
      //"profilbilid": //Profilbild muss seperat geschickt werden
      "beschreibung": "Balaklaknln"
    },
    "merkmal":{
        "alter":3, 
        "geschlecht": 1,
        "groesse": 125
    },
    "Tier":{
        "tierID": 2
    }
 }
"""
#untestet
#oben das json Format, welches /profil/create benötigt
@app.route("/profil/create", methods= ['POST'])
@token_required
def createProfil():
   dicUser = decodeToken(request.args.get('token'))
   json = request.get_json()
   try:
       merkmalID = callSoredProcReturn('create_merkmal', (json['merkmal']['alter'],json['merkmal']['geschlecht'],json['merkmal']['groesse'],json['Tier']['tierID'],0))
       #print(merkmalID[4])
       vormerkListeID = callSoredProcReturn('create_vormerkliste',(0,))
       likeListeID = callSoredProcReturn('create_likeliste',(0,))
       query = 'INSERT INTO Profil (profilName ,beschreibung,bewertungPositiv, bewertungNegativ, konto_email, Merkmal_merkmaleID, LikeListe_likelisteID, VormerkListe_vormerkListeID) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)'
       exeQuery(query, (json['profilInfo']['profilName'],json['profilInfo']['beschreibung'],0,0,dicUser['user'],merkmalID[4],likeListeID[0],vormerkListeID[0]), True)
   except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
   else:
       return Response(status=200)

#ungestestet
@app.route("/profil/uploadImage", methods= ['POST'])
@token_required
def uploadImage():
    dicUser = decodeToken(request.args.get('token'))
    
    profilName = request.form.get('profilName') 
    dirOfUser = UPLOAD_FOLDER + dicUser['user']
    if not os.path.exists(dirOfUser):
       os.makedirs(dirOfUser)

    if 'image'  in request.files:

        pic = request.files['image']
        newImageName =profilName + pathlib.Path(pic.filename).suffix
        path = os.path.join(dirOfUser, newImageName)
        pic.save(path)
        try:
            exeQuery('UPDATE Profil SET profilbild = %s WHERE profilName = %s AND konto_email = %s', (path, profilName, dicUser['user']), True)
        except mysql.connector.Error as e:  
            print(e) 
            return abort(400, '[ERROR]: '+str(e))
        else:
            return Response(status = 200)
    else:
        abort(400, '[ERROR]: form param "pic" ic missing')

 
@app.route("/profil/delete", methods= ['POST'])
@token_required
def deleteProfil():
    dicUser = decodeToken(request.args.get('token'))
    profilName = request.args.get('profilName')

    path = exeQuery('SELECT profilbild FROM Profil WHERE profilName = %s AND konto_email = %s', (profilName, dicUser['user']), False)
    
    print(path)
    if path[0][0] != None:
        try:
            os.remove(path[0][0])
        except OSError:
            pass

    try:
        fkID = exeQuery('SELECT Merkmal_merkmaleID,LikeListe_likelisteID,VormerkListe_vormerkListeID, profilID FROM Profil WHERE profilName = %s AND konto_email = %s', (profilName, dicUser['user']), False)
        exeQuery('DELETE FROM Profil WHERE profilName = %s AND konto_email = %s', (profilName, dicUser['user']), True)
        exeQuery('DELETE FROM Merkmal WHERE merkmaleID = %s', (fkID[0][0],), True)

        
            #reihenfolge wahrscheinlich umdrehen
        exeQuery('DELETE FROM LikeListe WHERE likelisteID = %s', (fkID[0][1],), True)
        exeQuery('DELETE FROM LikeListe_Profil WHERE Profil_profilID = %s', (fkID[0][3],), True)
        
        exeQuery('DELETE FROM VormerkListe WHERE vormerkListeID = %s', (fkID[0][2],), True)
        exeQuery('DELETE FROM VormerkListe_Profil WHERE Profil_profilID = %s AND Profil_konto_email = %s', (fkID[0][3], dicUser['user'],), True)
            
        exeQuery('DELETE FROM MatchListe WHERE Profil_profilID = %s AND Profil_konto_email = %s', (fkID[0][3], dicUser['user'],), True)
        exeQuery('DELETE FROM Profil_MatchListe WHERE Profil_profilID = %s AND Profil_konto_email = %s', (fkID[0][3], dicUser['user'],), True)
        
        exeQuery('DELETE FROM Suchfilter WHERE Profil_profilID = %s AND Profil_konto_email = %s', (fkID[0][3], dicUser['user'],), True)
            #macht vllt. mucken wegen der 1 im namen 
            #exeQuery('DELETE FROM FreundeBeziehung WHERE (Profil_profilID = %s AND Profil_konto_email = %s) OR (Profil_profilID 1 = %s AND Profil_konto_email 1 = %s)', (fkID[3], dicUser['user']), True)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    return Response(status = 200)

#Work in Progress. Maybe wrong return type
@app.route("/profil/getPic", methods= ['GET'])
@token_required
def getPic():
    dicUser = decodeToken(request.args.get('token'))
    profilName = request.args.get('profilName')

    path = exeQuery('SELECT profilbild FROM Profil WHERE profilName = %s AND konto_email = %s', (profilName, dicUser['user']), False)

    if path[0][0] != None:
        if os.path.isfile(path[0][0]):
           
            return send_file('pics/jj@gmail.de/DelProf.png')
        else:
            return abort(400, '[ERROR]: Image not found')
    else:
        return abort(400, '[ERROR]: No image stored')

#Work in Progress
@app.route("/profil/friendlist/addFriendRequest", methods= ['GET'])
@token_required
def getPic():
    dicUser = decodeToken(request.args.get('token'))
    userProfil = request.args.get('userProfil')

    #the infos of the profil who will get the request
    reciverProfilName = request.args.get('reciverProfilName')
    reciverEmail = request.args.get('reciveremail')
    #Todo autoincrement beziehungID
    exeQuery('INSERT INTO freundeBeziehung VALUES ')
    return Response(status = 200)

#what about pic? Can I send pic AND json
@app.route("/mostAttractive", methods= ['GET'])
def mostAttractive():
    numOfAcc = request.args.get('howMany') 
    print(numOfAcc)
    payload = []
    rowAsDict = {}
    try:
       data = exeQuery('SELECT * FROM Profil ORDER BY (bewertungPositiv - bewertungNegativ) DESC LIMIT 10', None, False)
       for row in data:
           rowAsDict = {'id': row[0], 'profilname': row[1], 'discription': row[3], 'positive':row[4], 'negative':row[5],'mail': row[6]}
           payload.append(rowAsDict)
    
       return jsonify(payload) #is it a problem [{...}, {...}] because of []
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))


   
@app.route("/static/<path:path>")
def send_static():
    return send_from_directory('static', path)


SWAGGER_URL = '/swagger'
API_URL = '/static/swagger.json'
SWAGGERUI_BLUEPRINT = get_swaggerui_blueprint(
    SWAGGER_URL,
    API_URL,
    config={
        'app_name': "REST-Dinder"
    }
)

app.register_blueprint(SWAGGERUI_BLUEPRINT, url_prefix=SWAGGER_URL)

if __name__ == "__main__":
    app.run(host="0.0.0.0", port = 5000)



    """
@app.route("/account/login", methods= ['POST'])
def login():
    email = request.form.get('email')
    pwd = request.form.get('pwd')
    if (email is None) or (pwd is None):
        return  abort(400, '[ERROR]: Vars have no values')

    else:
        data = exeQuery('SELECT EXISTS (SELECT 1 FROM konto WHERE email = %s AND passwort = %s)', (email, pwd), False)
        if data[0][0] == 1: 
            return Response(status = 200)
            #maybe return website?
        else:
            return  abort(400, '[ERROR]:Wrong email or passwort')

@app.route("/getGps", methods= ['POST'])
def getGPS():
    email = request.form.get('email')
    pwd = request.form.get('pwd')
    if (email is None) or (pwd is None):
        return  abort(400, '[ERROR]: Vars have no values')

    else:
        data = getDataFromDB('SELECT EXISTS (SELECT 1 FROM konto WHERE email = %s AND passwort = %s)', email, pwd)
        if data[0][0] == 1: 
            return getDataFromDB('SELECT standort FROM konto WHERE email = %', email)
        else:
            return  abort(400, '[ERROR]:Wrong email or passwort')
"""

""" def getDataFromDB(query, *data):
    
    try:
        myDatabase = mysql.connector.connect(**dbLoginInfo)
        cursor = myDatabase.cursor()
    except mysql.connector.Error as e:
        print('[ERROR WHILE CONNECTING TO DATABASE]: ', e)
    else:
        if data is None:
            cursor.execute(query)
            data = cursor.fetchall()
        else: 
            cursor.execute(query, data)
            data = cursor.fetchall()

        cursor.close()
        myDatabase.close()
        print(type(data))
    return data

def editDatabase(query, data):
    try:
        myDatabase = mysql.connector.connect(**dbLoginInfo)
        cursor = myDatabase.cursor()
    except mysql.connector.Error as e:
        print('[ERROR WHILE CONNECTING TO DATABASE]: ', e)
    else:
        if data != None:
            res = cursor.execute(query, data)
            myDatabase.commit()
        else:
            res = cursor.execute(query)
            myDatabase.commit() 

        cursor.close()
        myDatabase.close()
        return res """