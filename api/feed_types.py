from flask_restx import Namespace, Resource, fields
from .models import db, FeedType
from flask import request
from .decorators import active_user_required
from sqlalchemy import desc

api = Namespace('feed_types', description='Операции с типами корма')

feed_type_model = api.model('FeedType', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
})

@api.route('/')
class FeedTypeList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = FeedType.query
        name = request.args.get('name')
        if name:
            query = query.filter(FeedType.name.ilike(f'%{name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in FeedType.__table__.columns:
            col = getattr(FeedType, order_by)
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
    @api.expect(feed_type_model)
    def post(self):
        data = api.payload
        feed_type = FeedType(**data)
        db.session.add(feed_type)
        db.session.commit()
        return {'message': 'Тип корма добавлен', 'id': feed_type.id}, 201

@api.route('/<int:id>')
class FeedTypeResource(Resource):
    @active_user_required
    def get(self, id):
        feed_type = FeedType.query.get_or_404(id)
        return feed_type.to_dict()

    @active_user_required
    @api.expect(feed_type_model)
    def put(self, id):
        feed_type = FeedType.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(feed_type, key, value)
        db.session.commit()
        return {'message': 'Тип корма обновлен'}

    @active_user_required
    def delete(self, id):
        feed_type = FeedType.query.get_or_404(id)
        db.session.delete(feed_type)
        db.session.commit()
        return {'message': 'Тип корма удален'} 