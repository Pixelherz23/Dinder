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
#MErke ein User darf nicht zwei Profile haben die gleich heißen!!
#Demand paths:


#Prio:
#getMerkmale
#getAccInfo
#likeliste
#mnerkliste




#get Profils By name of merkmal (search)
#getUserInfo

#getFriendRequests

#changeData
#likeProfiland Add to Likelist
#everytime return email when getFriendlist
#change error return to jsonfiy
#default profilpic
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
@app.route("/account/getData", methods= ['GET'])
@token_required
def getData():
    user = decodeToken(request.args.get('token'))['user']
    try:
        query = 'SELECT * FROM konto  WHERE email = %s'
        data = exeQuery(query,(user,), False) #<= get error raise ValueError("Could not process parameters") when "," is missing

    except mysql.connector.Error as e: 
        print(e) 
        return jsonify('[ERROR]: '+str(e))
    else:
        return jsonify(data), 200

#untested
#when email changed what about the key (konto_email for example)
"""
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
"""

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

@app.route('/getGPS')
@token_required
def getGPS():
    
    dicUser = decodeToken(request.args.get('token'))
    
    try:
        #print(dicUser['user'])
        data = exeQuery('SELECT lat, lg FROM konto WHERE email = %s ', (dicUser['user'],), False)
        print(data[0][0])
        print(data[0][1])
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else: 
        return  jsonify({'lat' : data[0][0],'lg' : data[0][1]})

   # return  jsonify({'message' : 'This is only available for people with valid tokens.'})

#untested
@app.route('/setGPS', methods= ['POST'])
@token_required
def setGPS():
    lat = str(request.form.get('lat'))
    lg = str(request.form.get('lg'))
    dicUser = decodeToken(request.args.get('token')) 

    print(dicUser)
    try:
        print(dicUser['user'])
        data = exeQuery('UPDATE konto SET lat = %s , lg = %s WHERE email = %s ', (lat,lg,dicUser['user'],), True)
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

@app.route("/setStatus", methods= ['POST'])
@token_required
def setStatus():
    dicUser = decodeToken(request.args.get('token'))
    status = request.args.get('status')
    
    try:
        exeQuery('UPDATE konto SET istOnline = %s WHERE email = %s', ( status ,dicUser['user'],), False)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else: 
        return  Response(status = 200)



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
       query = 'INSERT INTO Profil (profilName, profilbild ,beschreibung,bewertungPositiv, bewertungNegativ, konto_email, Merkmal_merkmaleID, LikeListe_likelisteID, VormerkListe_vormerkListeID) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)'
       exeQuery(query, (json['profilInfo']['profilName'],'rest/pics/DefaultPic.png',json['profilInfo']['beschreibung'],0,0,dicUser['user'],merkmalID[4],likeListeID[0],vormerkListeID[0]), True)
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
        abort(400, '[ERROR]: form param "image" ic missing')




 
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
    print(path)
    if path[0][0] != None:
        if os.path.isfile(path[0][0]):
           path2 = '/home/kstieler/project_env/'+path[0][0]
           return send_file(path2)
        else:
            return abort(400, '[ERROR]: Image not found')
    else:
        return abort(400, '[ERROR]: No image stored')

@app.route("/profil/getPicFromOtherUser", methods= ['GET'])
@token_required
def getPicFromOtherUser():
    
    profilName = request.args.get('profilName')
    mail = request.args.get('mail')

    path = exeQuery('SELECT profilbild FROM Profil WHERE profilName = %s AND konto_email = %s', (profilName, mail), False)
    print(path)
    if path[0][0] != None:
        if os.path.isfile(path[0][0]):
            path2 = '/home/kstieler/project_env/'+path[0][0]
            return send_file(path2)
        else:
            return abort(400, '[ERROR]: Image not found')
    else:
        return abort(400, '[ERROR]: No image stored')

#tested
#Can the reciver send a friendrequest after he got the request??
@app.route("/profil/friendlist/addFriendRequest", methods= ['POST'])
@token_required
def addFriendRequest():
    reqUser = decodeToken(request.args.get('token'))['user']
    reqProfil = request.args.get('requestProfilName')

    reciverProfilName = request.args.get('reciverProfilName')
    reciverEmail = request.args.get('reciverEmail')
    try:
        reqProfilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (reqProfil,reqUser), False)
    
        reciverProfilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (reciverProfilName,reciverEmail), False)

        print(reqProfilID)
        print(reciverProfilID)
        #checkIfALreadyThere exeQuery('SELECT EXISTS(SELECT * FROM FreundeBeziehung WHERE  ) ')
        query = 'INSERT INTO FreundeBeziehung (Profil_profilID, Profil_konto_email, Profil_profilID1, Profil_konto_email1, istAkzeptiert) VALUES (%s,%s, %s, %s,%s)'
        exeQuery(query, (reqProfilID[0][0],reqUser,reciverProfilID[0][0],reciverEmail,0), True)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else:
        return Response(status = 200)

#testet
@app.route("/profil/friendlist/acceptFriendRequest", methods= ['POST'])
@token_required
def acceptFriendRequest():
    #the user/profil who accepts the request
    user = decodeToken(request.args.get('token'))['user']
    profilName = request.args.get('profilName')
    try:
        profilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (profilName,user), False)
    
        #the user/profil who sent the friendrequest
        reqProfil = request.args.get('requestFromProfil')
        reqUser = request.args.get('requestFromUser')
        reqProfilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (reqProfil,reqUser), False)

        query = 'UPDATE FreundeBeziehung SET istAkzeptiert = 1 WHERE Profil_profilID = %s AND Profil_profilID1 = %s AND Profil_konto_email1 = %s '
        exeQuery(query, (reqProfilID[0][0],profilID[0][0], user), True)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else:
        return Response(status = 200)

#tested
#use to delete a Friendship or to decline a Friendshiprequest
@app.route("/profil/friendlist/deleteFriendshipOrRequest", methods= ['POST'])
@token_required
def declineOrDeleteFriendRequest():
    #the user/profil who wants to delete the FriendRequest
    user = decodeToken(request.args.get('token'))['user']
    profilName = request.args.get('profilName')
    
    try:
        profilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (profilName,user), False)
        
        #the user/profil who is the victim :(
        notFriendProfil = request.args.get('notFriendProfil')
        notFriendUser = request.args.get('notFriendUser')
        notFriendProfilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (notFriendProfil,notFriendUser), False)

        query ='DELETE FROM FreundeBeziehung WHERE (Profil_profilID = %s AND Profil_profilID1 = %s AND Profil_konto_email1 = %s) OR (Profil_profilID = %s AND Profil_profilID1 = %s AND Profil_konto_email1 = %s)'
        exeQuery(query,(profilID[0][0],notFriendProfilID[0][0],notFriendUser, notFriendProfilID[0][0], profilID[0][0],user), True)
    
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else:
        return Response(status = 200)

#tested
@app.route("/profil/friendlist/getFriends", methods= ['GET'])
@token_required
def getFriends():
    user = decodeToken(request.args.get('token'))['user']
    profilName = request.args.get('profilName')
    try:
        profilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (profilName,user), False)
    except mysql.connector.Error as e:  
            print(e) 
            return abort(400, '[ERROR]: '+str(e))
    data = exeQuery('SELECT Profil_profilID, Profil_konto_email, Profil_profilID1, Profil_konto_email1 FROM FreundeBeziehung WHERE istAkzeptiert = 1 AND ((Profil_konto_email = %s AND Profil_profilID = %s) OR (Profil_konto_email1 = %s AND Profil_profilID1 = %s))', (user,profilID[0][0],user,profilID[0][0]), False)
    dictJson = {}
    
    for index, tuple in enumerate(data):
     
        if tuple[1] != user:
            try:
                profilName = exeQuery('SELECT profilName FROM Profil WHERE profilID = %s AND konto_email = %s ', (tuple[0],tuple[1]), False)
            except mysql.connector.Error as e:  
                print(e) 
                return abort(400, '[ERROR]: '+str(e))
            dictJson[index] = {'email': tuple[1], 'profil': profilName[0][0]}
          

        elif tuple[3] != user:
            try:
                profilName = exeQuery('SELECT profilName FROM Profil WHERE profilID = %s AND konto_email = %s ', (tuple[2],tuple[3]), False)
            except mysql.connector.Error as e:  
                print(e) 
                return abort(400, '[ERROR]: '+str(e))

            dictJson[index] = {'email': tuple[3], 'profil': profilName[0][0]}
           

    if dictJson is None: 
        return jsonify({'[ERROR]': 'No friendships found'}),204
    else:
        return jsonify(dictJson)


@app.route("/profil/friendlist/getFriendRequest", methods= ['GET'])
@token_required
def getFriendRequest():
    user = decodeToken(request.args.get('token'))['user']
    profilName = request.args.get('profilName')
    try:
        profilID = exeQuery('SELECT profilID FROM Profil WHERE profilName = %s AND konto_email = %s ', (profilName,user), False)
    except mysql.connector.Error as e:  
            print(e) 
            return abort(400, '[ERROR]: '+str(e))
    data = exeQuery('SELECT Profil_profilID, Profil_konto_email, Profil_profilID1, Profil_konto_email1 FROM FreundeBeziehung WHERE istAkzeptiert = 0 AND ((Profil_konto_email = %s AND Profil_profilID = %s) OR (Profil_konto_email1 = %s AND Profil_profilID1 = %s))', (user,profilID[0][0],user,profilID[0][0]), False)
    dictJson = {}
    
    for index, tuple in enumerate(data):
     
        if tuple[1] != user:
            try:
                profilName = exeQuery('SELECT profilName FROM Profil WHERE profilID = %s AND konto_email = %s ', (tuple[0],tuple[1]), False)
            except mysql.connector.Error as e:  
                print(e) 
                return abort(400, '[ERROR]: '+str(e))
            dictJson[index] = {'email': tuple[1], 'profil': profilName[0][0]}
          

        elif tuple[3] != user:
            try:
                profilName = exeQuery('SELECT profilName FROM Profil WHERE profilID = %s AND konto_email = %s ', (tuple[2],tuple[3]), False)
            except mysql.connector.Error as e:  
                print(e) 
                return abort(400, '[ERROR]: '+str(e))

            dictJson[index] = {'email': tuple[3], 'profil': profilName[0][0]}
           

    if dictJson is None: 
        return jsonify({'[ERROR]': 'No friendships found'}),204
    else:
        return jsonify(dictJson)


    
#what about pic? Can I send pic AND json
@app.route("/mostAttractive", methods= ['GET'])
def mostAttractive():
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

#A user can upvote multiple times???
@app.route("/setPositive", methods= ['POST'])
@token_required
def setPositive():
    profilName = request.args.get('profilName')
    mail = request.args.get('mail')
    #Watch out: what if multiple profils Better when profilID
    try:
        exeQuery('UPDATE Profil SET bewertungPositiv = bewertungPositiv + 1 WHERE profilName = %s AND konto_email = %s', (profilName, mail), True)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else:
        return Response(status = 200)
#testet
@app.route("/setNegative", methods= ['POST'])
@token_required
def setNegative():
    profilName = request.args.get('profilName')
    mail = request.args.get('mail')
    #Watch out: what if multiple profils Better when profilID
    try:
        exeQuery('UPDATE Profil SET bewertungNegativ = bewertungNegativ + 1 WHERE profilName = %s AND konto_email = %s', (profilName, mail), True)
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else:
        return Response(status = 200)
#testet
@app.route("/getJudgment", methods= ['GET'])
@token_required
def getJudgment():
    profilName = request.args.get('profilName')
    mail = request.args.get('mail')
    try:
        data = exeQuery('SELECT bewertungPositiv, bewertungNegativ FROM Profil WHERE profilName = %s AND konto_email = %s', (profilName, mail), False)
        return jsonify({'valuePositive':data[0][0],'valueNegative':data[0][1]}), 200
    except mysql.connector.Error as e:  
        print(e) 
        return abort(400, '[ERROR]: '+str(e))


@app.route("/profil/getProfilFromOtherUser", methods= ['GET'])
@token_required
def getProfilFromOtherUser():
    profilName = request.args.get('profilName')
    mail = request.args.get('mail')

    profilName = exeQuery('SELECT * FROM Profil WHERE  profilName= %s AND konto_email = %s ', (profilName,mail), False)
    dictProfilData = {
        'profilID':profilName[0][0],
        'profilName':profilName[0][1],
        'profilBildPfad':profilName[0][2],
        'beschreibung':profilName[0][3],
        'bewertungPositiv':profilName[0][4],
        'bewertungNegativ':profilName[0][5],
        'konto_email':profilName[0][6],
        'Merkmale_merkmalID':profilName[0][7],
        'LikeListe_likelisteID':profilName[0][8],
        'VormerkListe_vormerkListeID':profilName[0][9],

    }
    return jsonify(dictProfilData)

@app.route("/profil/getProfils", methods= ['GET'])
@token_required
def getProfiles():
    user = decodeToken(request.args.get('token'))['user']
    
    data = exeQuery('SELECT * FROM Profil WHERE konto_email = %s ', (user,), False)
    
    dictJson = {}
    for index, tuple in enumerate(data):
        dictJson[index] = {
            'profilID': tuple[0], 
            'profilName': tuple[1],
            'profilbild': tuple[2], 
            'beschreibung': tuple[3], 
            'bewertungPositiv': tuple[4], 
            'bewertungNegativ': tuple[5], 
            'konto_email': tuple[6], 
            'merkmalID': tuple[7], 
            'likeListeID': tuple[8], 
            'vormerkListe': tuple[9], 
            
             } 
    if dictJson is None: 
        return jsonify({'[ERROR]': 'No profils found'}),204
    else:
        return jsonify(dictJson)

"""
@app.route("/searchProfilsByFilter", methods= ['POST'])
@token_required
def searchProfilsByFilter():
    species = request.args.get('species')
    race = request.args.get('race')
    gender = request.args.get('gender')

    query = 'SELECT * FROM Profil WHERE  '
    valAsDict = {'spezies':species,'rasse': race,'geschlecht' :gender,}
    for key in valAsDict:
            if valAsDict[key] is not None:
                query = query + ' AND ' + key+ '= %s'

   
    data = exeQuery(query, (species,race,gender), False)
    print(data)

    return Response(status = 200)
    """

"""
#untested
@app.route("/searchProfilsByFilterAndGPS", methods= ['GET'])
@token_required
def searchProfilsByFilterAndGPS():
    user = decodeToken(request.args.get('token'))['user']
    species = request.args.get('species')
    race = request.args.get('race')
    gender = request.args.get('gender')
    lat = request.args.get('lat')
    lg =  request.args.get('lg')
    distance = request.args.get('distance')

    query = 'SELECT konto_email FROM Profil INNER JOIN Merkmal ON Merkmal_merkmaleID = merkmaleID INNER JOIN Tier ON Tier_tierID = tierID WHERE '
    
    valAsDict = {'spezies':species,'rasse': race,'geschlecht' :gender}
    tupleMerkmale = ()
    if species != "":
        tupleMerkmale = tupleMerkmale + (valAsDict['spezies'],)
        query = query +  'spezies = %s' + ' AND '
    
    if race != "":
        tupleMerkmale = tupleMerkmale + (valAsDict['rasse'],)
        query = query +  'rasse = %s' + ' AND '
    
    if gender != "":
        tupleMerkmale = tupleMerkmale + (valAsDict['geschlecht'],)
        query = query +  'geschlecht = %s'

    print(query)
    print(tupleMerkmale)
    dataByFilter = exeQuery(query, tupleMerkmale, False)
    
    dataByGPS = exeQuery('CALL get_accounts_in_range(%s,%s,%s, %s)', (lat ,lg, distance,user), False)
    

    if not dataByGPS : 
        return jsonify({'[ERROR]': 'No profils nearby'}),204
    else:
        dictJson = {}
        for index, tuple in enumerate(dataByGPS):
             for indexFilter, tupleFilter in enumerate(dataByFilter):
                if tuple[6] == tupleFilter[6]:
                    dictJson[index] = {
                        'profilID': tuple[0], 
                        'profilName': tuple[1],
                        'profilbild': tuple[2], 
                        'beschreibung': tuple[3],
                        'bewertungPositiv': tuple[4],
                        'bewertungNegativ': tuple[5],
                        'konto_email': tuple[6],
                        'merkmalID': tuple[7],
                        'likeListeID': tuple[8],
                        'vormerkListe': tuple[9], 
                        } 
    if not dictJson: 
        return jsonify({'[ERROR]': 'No profils found'}),204
    else:
        return jsonify(dictJson)
"""

@app.route("/searchProfilsByGPS", methods= ['GET'])
@token_required
def searchProfilsByGPS():
    user = decodeToken(request.args.get('token'))['user']
    lat = request.args.get('lat')
    lg =  request.args.get('lg')
    distance = request.args.get('distance')

    try:
        data= exeQuery('CALL get_accounts_in_range(%s,%s,%s, %s)', (lat,lg, distance, user), False)
        print(data)
        
    except mysql.connector.Error as e: 
        return jsonify({'[ERROR]': str(e)}),400

    if not data : 
        return jsonify({'[ERROR]': 'No profils nearby'}),204
    else:
        dictJson = {}
        for index, tuple in enumerate(data):
            dictJson[index] = {
                'profilID': tuple[0], 
                'profilName': tuple[1],
                'profilbild': tuple[2], 
                'beschreibung': tuple[3], 
                'bewertungPositiv': tuple[4], 
                'bewertungNegativ': tuple[5], 
                'konto_email': tuple[6], 
                'merkmalID': tuple[7], 
                'likeListeID': tuple[8], 
                'vormerkListe': tuple[9], 
                } 
    
        return jsonify(dictJson), 200


@app.route("/testProcedure", methods= ['GET'])
#@token_required
def testProcedure():
    data= exeQuery('CALL get_accounts_in_range(%s,%s,%s)', (52.327733 , 10.200029, 500), False)
    
    print(data)
    return Response(status = 200)


@app.route("/test2", methods= ['GET'])
#@token_required
def test2():
    x = request.args.get('lat')
    print(x )
    if x == "" :
        print("si")
    return Response(status = 200)


    
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
