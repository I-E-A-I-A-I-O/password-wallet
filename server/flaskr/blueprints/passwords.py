from datetime import datetime, timezone
from flask import request, Blueprint, current_app
from flask.json import jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from ..utils.crypto import decrypt, encrypt
from ..database.models import Passwords, Users, db
from http import HTTPStatus
from werkzeug.security import check_password_hash

password_blueprint = Blueprint("passwords", __name__, url_prefix= "/passwords")

@password_blueprint.route("/", methods=["POST"])
@jwt_required()
def get_post_password():
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

@password_blueprint.route("/list", methods=["POST"])
@jwt_required()
def list_user_passwords():
    user_identity = get_jwt_identity()
    body = None
    
    try:
        body = request.get_json()
    except:
        current_app.logger.exception(f"Bad body type received in list passwords from user {user_identity} at {datetime.now(timezone.utc)}")
        return jsonify({"message": "Bad body."}), HTTPStatus.BAD_REQUEST
    
    master_pass = body["password"]
    user = db.session.query(Users).filter_by(id=user_identity).scalar()
    same = check_password_hash(user.password, master_pass)

    if not same:
        return jsonify({"message": "Incorrect password"}), HTTPStatus.UNAUTHORIZED
    
    decrypted_key = decrypt(user.key, master_pass)
    saved_passwords = db.session.query(Passwords).filter_by(user=user_identity).all()
    decrypted_passwords = list()

    for saved_password in saved_passwords:
        decrypted_passwords.append(
            {
                "id": saved_password.id,
                "description": saved_password.name,
                "password": decrypt(saved_password.password, decrypted_key)
            }
        )
    
    return jsonify(decrypted_passwords), HTTPStatus.OK

@password_blueprint.route("/<string:password_id>", methods=["DELETE"])
@jwt_required()
def deletePassword(password_id: str):
    identity = get_jwt_identity()
    result = db.session.query(Passwords).filter_by(id=password_id).first()

    if result is None:
        current_app.logger.error(f"User {identity} attempted to delete not existend password with id {password_id}")
        return jsonify({"message": "Not found."}), HTTPStatus.NOT_FOUND
    
    if str(result.user) != str(identity):
        current_app.logger.error(f"User {identity} attempted to delete a foreign password with id {password_id} belonging to the user {str(result.user)}")
        return jsonify({"message": "Not your password. e.e"}), HTTPStatus.UNAUTHORIZED

    try:
        db.session.query(Passwords).filter(Passwords.id == password_id).delete()
        db.session.commit()
        current_app.logger.info(f"User {identity} deleted password {password_id}")
        return jsonify({"message": "Password deleted."}), HTTPStatus.OK
    except:
        current_app.logger.exception(f"Error deleting password {password_id} from user {identity}")
        return jsonify({"message": "Error deleting password."}), HTTPStatus.INTERNAL_SERVER_ERROR
