from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import Column, Integer, String, ForeignKey
import sqlalchemy.dialects.postgresql
from sqlalchemy.orm import relationship

db = SQLAlchemy()

class Users(db.Model):
    __tablename__ = 'users'
    
    id = Column(primary_key= True, type_=Integer)
    name = Column(type_=String())
    email = Column(type_=String(), unique=True)
    password = Column(type_=String())
    passwords = relationship("Passwords", backref="users", lazy=True)

    def __init__(self, name, email, password) -> None:
        self.name = name
        self.email = email
        self.password = password

    def __repr__(self) -> str:
        return "<name {}, email {}, id {}>".format(self.name, self.email, self.id)

    def serialize(self):
        return {
            "id": self.id,
            "name": self.name,
            "email": self.email,
            "password": self.password
        }

class Passwords(db.Model):
    __tablename__ = "passwords"

    id = Column(primary_key=True, type_=Integer)
    name = Column(type_=String())
    password = Column(type_=String())
    user = Column("user", Integer, ForeignKey("users.id"), nullable=False)

    def __init__(self, name, password, user):
        self.name = name
        self.password = password
        self.user = user

    def __repr__(self) -> str:
        return "<name {}, id {}>".format(self.name, self.id)

    def serialize(self):
        return {
            "id": self.id,
            "name": self.name,
            "password": self.password
        }

