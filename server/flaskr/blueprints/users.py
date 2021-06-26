from http import HTTPStatus
from flask import Blueprint, request, current_app
from ..database.models import Users, db
from werkzeug.security import generate_password_hash, check_password_hash
from ..utils.crypto import gen_random_key

bp = Blueprint("users", __name__, url_prefix='/users')

@bp.route("/", methods=(["GET", "POST"]))
def registerUsers():
    if request.method == "GET":
        return "Nothing yet", HTTPStatus.NOT_IMPLEMENTED

    if request.method == "POST":
        body = None
        
        try:
            body = request.get_json()
        except:
            current_app.logger.exception("Received request without JSON body")
            response_body = {
                "message": "Bad body type"
            }
            return response_body, HTTPStatus.BAD_REQUEST

        name = body["name"]
        email = body["email"]
        password = body["password"]
        key = gen_random_key(password)
        password = generate_password_hash(password)
        user = Users(
            name,
            email,
            password,
            key
        )
        
        try:
            db.session.add(user)
            db.session.commit()
            response_body = {
                "message": "Account created."
            }
            current_app.logger.info("Account registered")
            return response_body, HTTPStatus.CREATED
        except:
            current_app.logger.exception("Error inserting new account")
            response_body = {
                "message": "Couldn't register the account. Try again later."
            }
            return response_body, HTTPStatus.INTERNAL_SERVER_ERROR
        
