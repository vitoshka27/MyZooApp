from flask import Flask, jsonify, request, send_from_directory
from flask_sqlalchemy import SQLAlchemy
from flask_restx import Api
from flask_cors import CORS
import os
from flask_jwt_extended import JWTManager, create_access_token, jwt_required, get_jwt_identity, verify_jwt_in_request, get_jwt
from werkzeug.security import check_password_hash, generate_password_hash
from functools import wraps
import logging
from flask_restx import Resource, fields
from datetime import timedelta, datetime

from .models import db
from .animals import api as animals_ns
from .staff import api as staff_ns
from .species import api as species_ns
from .enclosures import api as enclosures_ns
from .staff_categories import api as staff_categories_ns
from .climate_zones import api as climate_zones_ns
from .feeding_classifications import api as feeding_classifications_ns
from .feed_types import api as feed_types_ns
from .feed_items import api as feed_items_ns
from .supplier_feed_types import api as supplier_feed_types_ns
from .enclosure_neighbors import api as enclosure_neighbors_ns
from .incompatible_species import api as incompatible_species_ns
from .animal_movement_history import api as animal_movement_history_ns
from .animal_caretakers import api as animal_caretakers_ns
from .category_attributes import api as category_attributes_ns
from .staff_attribute_values import api as staff_attribute_values_ns
from .decorators import role_required, superuser_required
from .diseases import api as diseases_ns
from .vaccines import api as vaccines_ns
from .animal_vaccinations import api as animal_vaccinations_ns
from .animal_diseases import api as animal_diseases_ns
from .animal_medical_records import api as animal_medical_records_ns
from .feed_suppliers import api as feed_suppliers_ns
from .feed_inventory import api as feed_inventory_ns
from .feed_production import api as feed_production_ns
from .animal_diet_requirements import api as animal_diet_requirements_ns
from .daily_feeding_menu import api as daily_feeding_menu_ns
from .feed_orders import api as feed_orders_ns
from .zoo_exchanges import api as zoo_exchanges_ns
from .custom_queries import api as custom_queries_ns
app = Flask(__name__)
CORS(app)


DB_USER     = os.getenv('DB_USER')
DB_PASSWORD = os.getenv('DB_PASSWORD')
DB_HOST     = os.getenv('DB_HOST')
DB_PORT     = os.getenv('DB_PORT')
DB_NAME     = os.getenv('DB_NAME')
app.config['SQLALCHEMY_DATABASE_URI'] = (
    f'mysql+pymysql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}'
)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['JWT_SECRET_KEY'] = os.getenv('JWT_SECRET_KEY', 'super-secret-key')
app.config['JWT_ACCESS_TOKEN_EXPIRES'] = timedelta(days=180)
jwt = JWTManager(app)

db.init_app(app)

authorizations = {
    'Bearer': {
        'type': 'apiKey',
        'in': 'header',
        'name': 'Authorization',
        'description': 'JWT Authorization header using the Bearer scheme. Example: "Bearer {token}"'
    }
}

api = Api(
    app,
    version='1.0',
    title='Zoo API',
    description='Zoo Management API',
    authorizations=authorizations,
    security='Bearer'
)
api.add_namespace(custom_queries_ns, path='/api/custom_queries')
api.add_namespace(animals_ns, path='/api/animals')
api.add_namespace(staff_ns, path='/api/staff')
api.add_namespace(species_ns, path='/api/species')
api.add_namespace(enclosures_ns, path='/api/enclosures')
api.add_namespace(staff_categories_ns, path='/api/staff_categories')
api.add_namespace(climate_zones_ns, path='/api/climate_zones')
api.add_namespace(feeding_classifications_ns, path='/api/feeding_classifications')
api.add_namespace(feed_types_ns, path='/api/feed_types')
api.add_namespace(feed_items_ns, path='/api/feed_items')
api.add_namespace(supplier_feed_types_ns, path='/api/supplier_feed_types')
api.add_namespace(enclosure_neighbors_ns, path='/api/enclosure_neighbors')
api.add_namespace(incompatible_species_ns, path='/api/incompatible_species')
api.add_namespace(animal_movement_history_ns, path='/api/animal_movement_history')
api.add_namespace(animal_caretakers_ns, path='/api/animal_caretakers')
api.add_namespace(category_attributes_ns, path='/api/category_attributes')
api.add_namespace(staff_attribute_values_ns, path='/api/staff_attribute_values')
api.add_namespace(diseases_ns, path='/api/diseases')
api.add_namespace(vaccines_ns, path='/api/vaccines')
api.add_namespace(animal_vaccinations_ns, path='/api/animal_vaccinations')
api.add_namespace(animal_diseases_ns, path='/api/animal_diseases')
api.add_namespace(animal_medical_records_ns, path='/api/animal_medical_records')
api.add_namespace(feed_suppliers_ns, path='/api/feed_suppliers')
api.add_namespace(feed_inventory_ns, path='/api/feed_inventory')
api.add_namespace(feed_production_ns, path='/api/feed_production')
api.add_namespace(animal_diet_requirements_ns, path='/api/animal_diet_requirements')
api.add_namespace(daily_feeding_menu_ns, path='/api/daily_feeding_menu')
api.add_namespace(feed_orders_ns, path='/api/feed_orders')
api.add_namespace(zoo_exchanges_ns, path='/api/zoo_exchanges')
# --- Logging setup ---
def setup_logging():
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s %(levelname)s %(name)s %(message)s',
        handlers=[
            logging.FileHandler("zoo_api.log", encoding='utf-8'),
            logging.StreamHandler()
        ]
    )

setup_logging()
logger = logging.getLogger("zoo_api")

login_model = api.model('Login', {
    'username': fields.String(required=True, description='Логин'),
    'password': fields.String(required=True, description='Пароль')
})

create_user_model = api.model('CreateUser', {
    'username': fields.String(required=True, description='Логин'),
    'password': fields.String(required=True, description='Пароль'),
    'last_name': fields.String(required=True, description='Фамилия'),
    'first_name': fields.String(required=True, description='Имя'),
    'middle_name': fields.String(description='Отчество'),
    'gender': fields.String(description='Пол'),
    'birth_date': fields.String(required=True, description='Дата рождения (YYYY-MM-DD)'),
    'hire_date': fields.String(required=True, description='Дата найма (YYYY-MM-DD)'),
    'salary': fields.Float(required=True, description='Зарплата'),
    'category_id': fields.Integer(required=True, description='ID категории сотрудника')
})

change_password_model = api.model('ChangePassword', {
    'old_password': fields.String(required=True, description='Старый пароль'),
    'new_password': fields.String(required=True, description='Новый пароль')
})

UPLOAD_FOLDER = os.path.abspath(os.getenv('AVATAR_UPLOAD_FOLDER', 'media/avatars'))
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

@app.route('/api/profile/avatar', methods=['POST'])
@jwt_required()
def upload_avatar():
    from .models import Staff
    logger.info(f"FILES: {request.files}")
    if 'avatar' not in request.files or 'avatar_original' not in request.files:
        logger.info("NO FILE PART (avatar or avatar_original)")
        return {'msg': 'No file part (avatar or avatar_original)'}, 400
    avatar_file = request.files['avatar']
    original_file = request.files['avatar_original']
    logger.info(f"FILENAME avatar: {avatar_file.filename}, original: {original_file.filename}")
    if avatar_file.filename == '' or original_file.filename == '':
        logger.info("NO SELECTED FILE")
        return {'msg': 'No selected file'}, 400
    if avatar_file and allowed_file(avatar_file.filename) and original_file and allowed_file(original_file.filename):
        user_id = get_jwt_identity()
        timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
        ext_avatar = avatar_file.filename.rsplit('.', 1)[1].lower()
        ext_original = original_file.filename.rsplit('.', 1)[1].lower()
        filename_avatar = f"user_{user_id}_{timestamp}_avatar.{ext_avatar}"
        filename_original = f"user_{user_id}_{timestamp}_original.{ext_original}"
        os.makedirs(UPLOAD_FOLDER, exist_ok=True)
        filepath_avatar = os.path.join(UPLOAD_FOLDER, filename_avatar)
        filepath_original = os.path.join(UPLOAD_FOLDER, filename_original)
        logger.info(f"SAVING TO: {filepath_avatar} and {filepath_original}")

        user = Staff.query.get(user_id)
        # Удаляем старые аватарки, если они есть
        if user.avatar_url:
            old_path = os.path.join(UPLOAD_FOLDER, os.path.basename(user.avatar_url))
            if os.path.exists(old_path):
                try:
                    os.remove(old_path)
                    logger.info(f"Removed old avatar: {old_path}")
                except Exception as e:
                    logger.warning(f"Failed to remove old avatar: {old_path} ({e})")
        if user.avatar_original_url:
            old_orig_path = os.path.join(UPLOAD_FOLDER, os.path.basename(user.avatar_original_url))
            if os.path.exists(old_orig_path):
                try:
                    os.remove(old_orig_path)
                    logger.info(f"Removed old original avatar: {old_orig_path}")
                except Exception as e:
                    logger.warning(f"Failed to remove old original avatar: {old_orig_path} ({e})")

        avatar_file.save(filepath_avatar)
        original_file.save(filepath_original)
        user.avatar_url = f"/media/avatars/{filename_avatar}"
        user.avatar_original_url = f"/media/avatars/{filename_original}"
        db.session.commit()
        logger.info(f"SAVED: {user.avatar_url}, {user.avatar_original_url}")
        return {'avatar_url': user.avatar_url, 'avatar_original_url': user.avatar_original_url}
    logger.info("INVALID FILE")
    return {'msg': 'Invalid file'}, 400

@app.route('/media/avatars/<filename>')
def get_avatar(filename):
    return send_from_directory(UPLOAD_FOLDER, filename)

@api.route('/api/auth/login')
class LoginResource(Resource):
    @api.expect(login_model)
    def post(self):
        data = api.payload
        username = data.get('username')
        password = data.get('password')
        from .models import Staff
        user = Staff.query.filter_by(username=username, is_active=True).first()
        if not user or not check_password_hash(user.password_hash, password):
            logger.warning(f"Failed login attempt for username: {username}")
            return {'msg': 'Неверный логин или пароль'}, 401
        access_token = create_access_token(
            identity=str(user.id),
            additional_claims={
                'role': user.category_id,
            }
        )
        logger.info(f"User {user.id} ({user.username}) logged in.")
        return {'access_token': access_token, 'user_id': user.id, 'role': user.category_id}

@api.route('/api/admin/protected')
class AdminProtectedResource(Resource):
    @jwt_required()
    @role_required([5])  # Только администрация (category_id=5)
    def get(self):
        return {'msg': 'Доступ разрешён только администрации'}

@api.route('/api/admin/create_user')
class AdminCreateUserResource(Resource):
    @jwt_required()
    @superuser_required
    @api.expect(create_user_model)
    def post(self):
        from .models import Staff
        data = api.payload
        if Staff.query.filter_by(username=data['username']).first():
            logger.warning(f"Attempt to create user with existing username: {data['username']}")
            return {'msg': 'Пользователь с таким логином уже существует'}, 409
        password_hash = generate_password_hash(data['password'])
        staff = Staff(
            username=data['username'],
            password_hash=password_hash,
            last_name=data['last_name'],
            first_name=data['first_name'],
            middle_name=data.get('middle_name', ''),
            gender=data.get('gender'),
            birth_date=data['birth_date'],
            hire_date=data['hire_date'],
            salary=data['salary'],
            category_id=data['category_id'],
            is_active=True
        )
        db.session.add(staff)
        db.session.commit()
        logger.info(f"Superuser created new user: {staff.username} (id={staff.id})")
        return {'msg': 'Пользователь создан', 'user_id': staff.id}, 201

@api.route('/api/auth/me')
class MeResource(Resource):
    @jwt_required()
    def get(self):
        from .models import Staff, StaffCategory
        identity = get_jwt_identity()
        user = Staff.query.get(identity)
        if not user:
            return {'msg': 'Пользователь не найден'}, 404
        category = StaffCategory.query.get(user.category_id)
        return {
            'id': user.id,
            'username': user.username,
            'last_name': user.last_name,
            'first_name': user.first_name,
            'middle_name': user.middle_name,
            'category_id': user.category_id,
            'category_name': category.name if category else None,
            'role': user.category_id,
            'is_active': user.is_active,
            'birth_date': user.birth_date.isoformat() if user.birth_date else None,
            'hire_date': user.hire_date.isoformat() if user.hire_date else None,
            'avatar_url': user.avatar_url if hasattr(user, 'avatar_url') else None,
            'avatar_original_url': user.avatar_original_url if hasattr(user, 'avatar_original_url') else None
        }

@api.route('/api/admin/block_user/<int:user_id>')
class AdminBlockUserResource(Resource):
    @jwt_required()
    @superuser_required
    def post(self, user_id):
        from .models import Staff
        user = Staff.query.get(user_id)
        if not user:
            logger.warning(f"Attempt to block non-existent user id={user_id}")
            return {'msg': 'Пользователь не найден'}, 404
        user.is_active = False
        db.session.commit()
        logger.info(f"Superuser blocked user id={user_id}")
        return {'msg': 'Пользователь заблокирован'}

@api.route('/api/admin/unblock_user/<int:user_id>')
class AdminUnblockUserResource(Resource):
    @jwt_required()
    @superuser_required
    def post(self, user_id):
        from .models import Staff
        user = Staff.query.get(user_id)
        if not user:
            logger.warning(f"Attempt to unblock non-existent user id={user_id}")
            return {'msg': 'Пользователь не найден'}, 404
        user.is_active = True
        db.session.commit()
        logger.info(f"Superuser unblocked user id={user_id}")
        return {'msg': 'Пользователь разблокирован'}

@api.route('/api/admin/delete_user/<int:user_id>')
class AdminDeleteUserResource(Resource):
    @jwt_required()
    @superuser_required
    def delete(self, user_id):
        from .models import Staff
        user = Staff.query.get(user_id)
        if not user:
            logger.warning(f"Attempt to delete non-existent user id={user_id}")
            return {'msg': 'Пользователь не найден'}, 404
        db.session.delete(user)
        db.session.commit()
        logger.info(f"Superuser deleted user id={user_id}")
        return {'msg': 'Пользователь удалён'}

@api.route('/api/auth/change_password')
class ChangePasswordResource(Resource):
    @jwt_required()
    @api.expect(change_password_model)
    def post(self):
        from .models import Staff
        identity = get_jwt_identity()
        user = Staff.query.get(identity)
        data = api.payload
        if not user or not check_password_hash(user.password_hash, data['old_password']):
            logger.warning(f"User id={identity} failed password change (wrong old password)")
            return {'msg': 'Старый пароль неверен'}, 400
        user.password_hash = generate_password_hash(data['new_password'])
        db.session.commit()
        logger.info(f"User id={identity} changed password")
        return {'msg': 'Пароль успешно изменён'}

# Пример ограничения доступа для ветеринара (category_id=1)
@api.route('/api/vet/protected')
class VetProtectedResource(Resource):
    @jwt_required()
    @role_required([1, 5])
    def get(self):
        return {'msg': 'Доступ разрешён только ветеринару'}

# Пример ограничения доступа для дрессировщика (category_id=3)
@api.route('/api/trainer/protected')
class TrainerProtectedResource(Resource):
    @jwt_required()
    @role_required([3, 5])
    def get(self):
        return {'msg': 'Доступ разрешён только дрессировщику'}

@app.errorhandler(Exception)
def handle_exception(e):
    from werkzeug.exceptions import HTTPException
    if isinstance(e, HTTPException):
        logger.error(f"HTTPException: {e.code} {e.name} - {e.description}")
        return jsonify({
            'error': {
                'code': e.code,
                'name': e.name,
                'description': e.description
            }
        }), e.code
    logger.exception(f"Unhandled Exception: {str(e)}")
    return jsonify({
        'error': {
            'code': 500,
            'name': 'Internal Server Error',
            'description': str(e)
        }
    }), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True) 