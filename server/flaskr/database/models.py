from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import Column, Integer, String, ForeignKey, DateTime
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.orm import relationship
import uuid

db = SQLAlchemy()

class Users(db.Model):
    __tablename__ = 'users'
    
    id = Column(type_=UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    name = Column(type_=String())
    email = Column(type_=String(), unique=True)
    password = Column(type_=String())
    key = Column(type_=String())
    passwords = relationship("Passwords", backref="users", lazy=True)

    def __init__(self, name, email, password, key) -> None:
        self.name = name
        self.email = email
        self.password = password
        self.key = key

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

    id = Column(type_=UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    name = Column(type_=String())
    password = Column(type_=String())
    user = Column("user", UUID(as_uuid=True), ForeignKey("users.id"), nullable=False)

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

class JWTModel(db.Model):
    __tablename__ = "tokens"

    id = Column(type_=UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    jti = Column(type_=String(), nullable=False)
    created_at = Column(type_=DateTime, nullable=False)

    def __init__(self, jti, created_at):
        self.jti = jti
        self.created_at = created_at

    def __repr__(self) -> str:
        return "<jti {}, id{}, created_at {}".format(self.jti, self.id, self.created_at)

    def serialize(self):
        return {
            "id": self.id,
            "jti": self.name,
            "created_at": self.created_at
        }
