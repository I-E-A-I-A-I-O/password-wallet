from flask_jwt_extended import JWTManager
from ..database.models import db, JWTModel

jwt = JWTManager()

@jwt.revoked_token_loader
def check_if_token_revoked(jwt_header, jwt_payload):
    jti = jwt_payload["jti"]
    token = db.session.query(JWTModel).filter_by(jti=jti).scalar()
    return token is not None
