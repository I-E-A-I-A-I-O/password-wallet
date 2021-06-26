from datetime import timedelta
from dotenv import load_dotenv
load_dotenv()
import os
from flask import Flask
from flask_migrate import Migrate

def create_app(test_config=None):
    ACCESS_EXPIRE = timedelta(hours=1)
    app = Flask(__name__, instance_relative_config=True)
    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
    app.config.from_mapping(
        SECRET_KEY=os.environ["SECRET_KEY"],
        SQLALCHEMY_DATABASE_URI=os.environ["DATABASE_URI"],
        JWT_SECRET_KEY=os.environ["JWT_SECRET_KEY"],
        JWT_ACCESS_TOKEN_EXPIRES=ACCESS_EXPIRE
    )

    from .utils.jwt import jwt

    jwt.init_app(app)

    from .blueprints.users import users_bp
    from .blueprints.session import session_bp
    
    app.register_blueprint(users_bp)
    app.register_blueprint(session_bp)

    from .database.models import db
    
    db.init_app(app)
    Migrate(app, db)

    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass
    
    return app
