from flask import (
    Blueprint, app, request, url_for, current_app, send_file
)
from ..database.models import Users, db

bp = Blueprint("test", __name__, url_prefix='/test')

@bp.route("/wow", methods=(["GET", "POST"]))
def testEnd():
    if request.method == "GET":
        return send_file("./downloads/Documents.zip", as_attachment=True)

    if request.method == "POST":
        name = request.form["name"]
        email = request.form["email"]
        password = request.form["password"]
        user = Users(
            name=name,
            email=email,
            password=password
        )
        db.session.add(user)
        db.session.commit()
        return "OK"