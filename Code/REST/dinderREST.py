from flask import Flask, request, Response, abort,jsonify,request,make_response
import jwt #jsonwebToken #v1.7.1. bei aktueller Version Fehler beim Wrapper decode: It is required that you pass in a value for the \"algorithms\" argument when calling decode().
import datetime
from flask.helpers import send_from_directory
from mysql.connector import Error
import mysql.connector
import os 
from flask_swagger_ui import get_swaggerui_blueprint
from werkzeug.exceptions import BadRequest
from functools import wraps


#TODO
# Maybe delete "," of tuple with multiple entries?
#Create account/profilfolder for pics
dbLoginInfo = {
    'host' : 'localhost',
    'port': '3306',
    'user': 'restAPI',
    'password' : '4537842',
    'database' : 'dindersql'
}

UPLOAD_FOLDER = '~/pics'
ALLOWED_EXTENSIONS = set(['txt', 'pdf', 'png', 'jpg', 'jpeg', 'gif'])

app = Flask(__name__)
app.config['SECRET_KEY'] = 'hgbkgjbkhvbjhv'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER



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
    #print(type(res))
    #print(res)
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

    for x in proc:
        print(x)


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
{"account": "jj@gmail.com",  
 "profilInfo": 
    {
      "profilName": "Doe",
      "profilbilid": "???",
      "beschreibung": "Balaklaknln"
    },
    "merkmal":{
        "alter":3, 
        "geschlecht": 1,
        "groesse": 125
    }
 }
 #Wat about Tier
"""
@app.route("/profil/create", methods= ['POST'])
def addEntry():
   data = request.get_json()
   print(type(data))
   print(data)
   print('----------')
   print( data['profilInfo']['profilName'])
   return Response(status=200)
   


""" try:
        query = ' '
        data = exeQuery(query,(), True)
    except mysql.connector.Error as e: 
        print(e) 
        return abort(400, '[ERROR]: '+str(e))
    else: 
        return  Response(status = 200)
"""
   





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