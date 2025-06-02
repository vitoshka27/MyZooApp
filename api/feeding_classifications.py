from flask_restx import Namespace, Resource, fields
from .models import db, FeedingClassification
from .decorators import active_user_required, role_required
from flask import request
from sqlalchemy import desc

api = Namespace('feeding_classifications', description='Операции с классификациями питания')

feeding_classification_model = api.model('FeedingClassification', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
})

@api.route('/')
class FeedingClassificationList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = FeedingClassification.query
        name = request.args.get('name')
        if name:
            query = query.filter(FeedingClassification.name.ilike(f'%{name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in FeedingClassification.__table__.columns:
            col = getattr(FeedingClassification, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 20, type=int)
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        items = [c.to_dict() for c in pagination.items]
        return {
            'total': pagination.total,
            'page': page,
            'limit': limit,
            'data': items
        }

    @active_user_required
    @role_required([5])
    @api.expect(feeding_classification_model)
    def post(self):
        data = api.payload
        classification = FeedingClassification(**data)
        db.session.add(classification)
        db.session.commit()
        return {'message': 'Классификация добавлена', 'id': classification.id}, 201

@api.route('/<int:id>')
class FeedingClassificationResource(Resource):
    @active_user_required
    def get(self, id):
        classification = FeedingClassification.query.get_or_404(id)
        return classification.to_dict()

    @active_user_required
    @role_required([5])
    @api.expect(feeding_classification_model)
    def put(self, id):
        classification = FeedingClassification.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(classification, key, value)
        db.session.commit()
        return {'message': 'Классификация обновлена'}

    @active_user_required
    @role_required([5])
    def delete(self, id):
        classification = FeedingClassification.query.get_or_404(id)
        db.session.delete(classification)
        db.session.commit()
        return {'message': 'Классификация удалена'} 