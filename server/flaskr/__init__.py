from dotenv import load_dotenv
load_dotenv()
import os
from flask import Flask
from flask_migrate import Migrate

def create_app(test_config=None):
    app = Flask(__name__, instance_relative_config=True)
    app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False
    app.config.from_mapping(
        SECRET_KEY=os.environ["SECRET_KEY"],
        SQLALCHEMY_DATABASE_URI=os.environ["DATABASE_URI"],
    )

    from .blueprints.users import bp
    
    app.register_blueprint(bp)

    from .database.models import db
    
    db.init_app(app)
    Migrate(app, db)

    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass
    
    return app
