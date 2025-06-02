from flask_restx import Namespace, Resource, fields
from .models import db, DailyFeedingMenu
from .decorators import active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('daily_feeding_menu', description='Операции с ежедневным меню кормления')

daily_feeding_menu_model = api.model('DailyFeedingMenu', {
    'id': fields.Integer(readonly=True),
    'animal_id': fields.Integer(required=True),
    'diet_id': fields.Integer(required=True),
    'feeding_number': fields.Integer(required=True),
    'feeding_date_time': fields.String(required=True),
    'feed_item_id': fields.Integer(required=True),
    'quantity': fields.Float(required=True),
})

@api.route('/')
class DailyFeedingMenuList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = DailyFeedingMenu.query
        animal_id = request.args.get('animal_id', type=int)
        diet_id = request.args.get('diet_id', type=int)
        if animal_id:
            query = query.filter_by(animal_id=animal_id)
        if diet_id:
            query = query.filter_by(diet_id=diet_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in DailyFeedingMenu.__table__.columns:
            col = getattr(DailyFeedingMenu, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [a.to_dict() for a in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @api.expect(daily_feeding_menu_model)
    def post(self):
        data = api.payload
        menu = DailyFeedingMenu(**data)
        db.session.add(menu)
        db.session.commit()
        return {'message': 'Меню добавлено', 'id': menu.id}, 201

@api.route('/<int:id>')
class DailyFeedingMenuResource(Resource):
    @active_user_required
    def get(self, id):
        menu = DailyFeedingMenu.query.get_or_404(id)
        return menu.to_dict()

    @active_user_required
    @api.expect(daily_feeding_menu_model)
    def put(self, id):
        menu = DailyFeedingMenu.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(menu, key, value)
        db.session.commit()
        return {'message': 'Меню обновлено'}

    @active_user_required
    def delete(self, id):
        menu = DailyFeedingMenu.query.get_or_404(id)
        db.session.delete(menu)
        db.session.commit()
        return {'message': 'Меню удалено'} 