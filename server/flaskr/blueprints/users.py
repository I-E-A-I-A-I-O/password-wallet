from http import HTTPStatus
from flask import Blueprint, request, current_app, jsonify
from ..database.models import Users, db
from werkzeug.security import generate_password_hash
from ..utils.crypto import gen_random_key

users_bp = Blueprint("users", __name__, url_prefix='/users')

@users_bp.route("/", methods=(["GET", "POST"]))
def registerUsers():
    if request.method == "GET":
        return "Nothing yet", HTTPStatus.NOT_IMPLEMENTED

    if request.method == "POST":
        body = None
        
        try:
            body = request.get_json()
        except:
            current_app.logger.exception("Received request without JSON body")
            return jsonify(message="Bad body."), HTTPStatus.BAD_REQUEST

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
            current_app.logger.info("Account registered")
            return jsonify(message="Account created."), HTTPStatus.CREATED
        except:
            current_app.logger.exception("Error inserting new account")
            return jsonify(message="Couldn't register the account. Try again later."), HTTPStatus.INTERNAL_SERVER_ERROR
        
