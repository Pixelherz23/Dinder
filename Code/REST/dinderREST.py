from flask import Flask, request, Response, abort
from flask.helpers import send_from_directory
from mysql.connector import Error
import mysql.connector
from flask_swagger_ui import get_swaggerui_blueprint
from werkzeug.exceptions import BadRequest





app = Flask(__name__)

dbLoginInfo = {
    'host' : 'localhost',
    'port': '3306',
    'user': 'restAPI',
    'password' : '4537842',
    'database' : 'dindersql'
}


def getDataFromDB(query):
    
    try:
        myDatabase = mysql.connector.connect(**dbLoginInfo)
        cursor = myDatabase.cursor()
    except mysql.connector.Error as e:
        print('[ERROR WHILE CONNECTING TO DATABASE]: ', e)
    else:
        cursor.execute(query)
        data = cursor.fetchall()

        cursor.close()
        myDatabase.close()

    return str(data)

def editDatabase(query, data):
    try:
        myDatabase = mysql.connector.connect(**dbLoginInfo)
        cursor = myDatabase.cursor()
    except mysql.connector.Error as e:
        print('[ERROR WHILE CONNECTING TO DATABASE]: ', e)
    else:
        if data != None:
            cursor.execute(query, data)
            myDatabase.commit()
        else:
            cursor.execute(query)
            myDatabase.commit() 

        cursor.close()
        myDatabase.close()

def callStoredProc(procName, *data):
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



    

@app.route("/test")
def testMethod():
    return getDataFromDB("SELECT * from konto")


@app.route("/account/new", methods= ['POST'])
def newAccount():
    #response 
    email = request.form.get('email')
    firstname = request.form.get('firstname')
    lastname = request.form.get('lastname')
    phoneNum = request.form.get('phoneNum')
    institution = request.form.get('institution')
    birthday = request.form.get('birthday')
    

    isOnline = 0
    inactiv = 0
    isBanned = 0
    try:
        callStoredProc('create_account', email,lastname,firstname, phoneNum,birthday, passwort, institution,isOnline, inactiv, isBanned)

    except mysql.connector.Error as e:  
        print(e) 

        return abort(400, '[ERROR]: '+str(e))
       
    else:
        return Response(status = 200)

@app.route("/account/login", methods= ['GET'])
def login():
    email = request.form.get('email')
    pwd = request.form.get('pwd')

    if (email is None) or (pwd is None):
        return  abort(400, '[ERROR]: Vars have no values')
    else: 
        pwdDB = getDataFromDB('Sele')










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