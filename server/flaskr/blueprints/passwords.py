from datetime import datetime, timezone
from flask import request, Blueprint, current_app
from flask.json import jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from ..utils.crypto import decrypt, encrypt
from ..database.models import Passwords, Users, db
from http import HTTPStatus
from werkzeug.security import check_password_hash

password_blueprint = Blueprint("passwords", __name__, url_prefix= "/passwords")

@password_blueprint.route("/", methods=["POST", "GET"])
@jwt_required()
def get_post_password():
    if request.method == "GET":
        return jsonify(nada="nada"), HTTPStatus.NOT_IMPLEMENTED
    if request.method == "POST":
        token_identity = get_jwt_identity()
        body = None

        try:
            body = request.get_json()
        except:
            current_app.logger.exception("Bad request body received in passwords post")
            return jsonify({"message": "Bad body."}), HTTPStatus.BAD_REQUEST
        
        description = body["description"]
        password = body["password"]
        master_password = body["master_pass"]
        user = db.session.query(Users).filter_by(id=token_identity).scalar()

        if user is None:
            current_app.logger.error(f"User not found from identity {token_identity} in post password method at {datetime.now(timezone.utc)}")
            return jsonify({"message": "User not found."}), HTTPStatus.NOT_FOUND
        
        same = check_password_hash(user.password, master_password)

        if not same:
            current_app.logger.error(f"Attemp to save password with incorrect master pass {master_password} for user {token_identity} at {datetime.now(timezone.utc)}")
            return jsonify({"message": "Incorrect password."}), HTTPStatus.UNAUTHORIZED
        
        decrypted_key = decrypt(user.key, master_password)
        encrypted_password = encrypt(password, decrypted_key)

        password_obj = Passwords(
            description,
            encrypted_password,
            user.id
        )

        try:
            db.session.add(password_obj)
            db.session.commit()
            return jsonify({"message": "OK", "id": password_obj.id}), HTTPStatus.OK
        except:
            current_app.logger.exception(f"Exception trying to inster new password field for user {user.id} at {datetime.now(timezone.utc)}")
            return jsonify({"message": "Error saving the password."}), HTTPStatus.INTERNAL_SERVER_ERROR

