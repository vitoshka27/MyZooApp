from functools import wraps
from flask_jwt_extended import verify_jwt_in_request, get_jwt, get_jwt_identity
from .models import Staff

def role_required(category_ids):
    def decorator(fn):
        @wraps(fn)
        def wrapper(*args, **kwargs):
            verify_jwt_in_request()
            identity = get_jwt_identity()
            user = Staff.query.get(identity)
            if not user or not user.is_active:
                return {'msg': 'Пользователь не найден или неактивен'}, 401
            claims = get_jwt()
            # Доступ разрешён только если роль 5 (администрация) или роль в списке
            if not (claims.get('role') == 5 or claims.get('role') in category_ids):
                return {'msg': 'Недостаточно прав'}, 403
            return fn(*args, **kwargs)
        return wrapper
    return decorator

def superuser_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        verify_jwt_in_request()
        identity = get_jwt_identity()
        user = Staff.query.get(identity)
        if not user or not user.is_active:
            return {'msg': 'Пользователь не найден или неактивен'}, 401
        claims = get_jwt()
        if claims.get('role') != 5:
            return {'msg': 'Доступ только для администратора БД/API'}, 403
        return fn(*args, **kwargs)
    return wrapper

def active_user_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        verify_jwt_in_request()
        identity = get_jwt_identity()
        user = Staff.query.get(identity)
        if not user or not user.is_active:
            return {'msg': 'Пользователь не найден или неактивен'}, 401
        return fn(*args, **kwargs)
    return wrapper 