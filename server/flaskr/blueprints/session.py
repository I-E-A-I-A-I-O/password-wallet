from datetime import datetime, timezone
from flask import Blueprint, request, current_app, jsonify
from ..database.models import JWTModel, Users, db
from werkzeug.security import check_password_hash
from ..utils.crypto import gen_random_key
from http import HTTPStatus
from flask_jwt_extended import (
    create_access_token, 
    jwt_required, get_jwt, 
    create_refresh_token,
    get_jwt_identity
    )

session_bp = Blueprint("session", __name__, url_prefix="/session")

@session_bp.route("/login", methods=["POST"])
def login():
    body = None

    try:
        body = request.get_json()
    except:
        current_app.logger.exception("Received request body in incorrect format.")
        return jsonify(message="Bad body."), HTTPStatus.BAD_REQUEST

    email = body["email"]
    password = body["password"]
    users = None

    try:
        users = db.session.query(Users).filter_by(email=email).scalar()
    except:
        current_app.logger.exception("Error selecting users from email in login. Database error")
        return jsonify(message="Couldn't complete login."), HTTPStatus.INTERNAL_SERVER_ERROR
    
    if users is None:
        return jsonify(message="Email not found."), HTTPStatus.NOT_FOUND
    else:
        same = check_password_hash(users.password, password)
        if (same):
            access_token = create_access_token(users.id)
            refresh_token = create_refresh_token(users.id)
            return jsonify({
                "message": "OK",
                "name": users.name,
                "token": access_token,
                "refresh_token": refresh_token
            }), HTTPStatus.CREATED
        else:
            return jsonify(message="Incorrect password."), HTTPStatus.UNAUTHORIZED

@session_bp.route("/logout", methods=["DELETE"])
@jwt_required()
def logout():
    jti = get_jwt()["jti"]
    now = datetime.now(timezone.utc)

    try:
        db.session.add(JWTModel(jti, now))
        db.session.commit()
        return jsonify(message="Session closed."), HTTPStatus.OK
    except:
        current_app.logger.exception("Error revoking jwt")
        return jsonify(message="Error ending session."), HTTPStatus.INTERNAL_SERVER_ERROR

@session_bp.route("/token/refresh", methods=["POST"])
@jwt_required(refresh=True)
def refresh():
    identity = get_jwt_identity()
    token = create_access_token(identity)
    return jsonify({
        "token": token
    }), HTTPStatus.CREATED

@session_bp.route("/token/state", methods=["GET"])
@jwt_required()
def testToken():
    return jsonify({"message": "OK"}), HTTPStatus.OK
