from flask_restx import Namespace, Resource, fields
from .models import db, ClimateZone
from flask import request
from sqlalchemy import desc
from .decorators import active_user_required

api = Namespace('climate_zones', description='Операции с климатическими зонами')

climate_zone_model = api.model('ClimateZone', {
    'id': fields.Integer(readonly=True),
    'name': fields.String(required=True),
})

@api.route('/')
class ClimateZoneList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = ClimateZone.query
        name = request.args.get('name')
        if name:
            query = query.filter(ClimateZone.name.ilike(f'%{name}%'))
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in ClimateZone.__table__.columns:
            col = getattr(ClimateZone, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        items = [a.to_dict() for a in query.all()]
        return {
            'total': len(items),
            'data': items
        }

    @api.expect(climate_zone_model)
    def post(self):
        data = api.payload
        zone = ClimateZone(**data)
        db.session.add(zone)
        db.session.commit()
        return {'message': 'Климатическая зона добавлена', 'id': zone.id}, 201

@api.route('/<int:id>')
class ClimateZoneResource(Resource):
    @active_user_required
    def get(self, id):
        zone = ClimateZone.query.get_or_404(id)
        return zone.to_dict()

    @active_user_required
    @api.expect(climate_zone_model)
    def put(self, id):
        zone = ClimateZone.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(zone, key, value)
        db.session.commit()
        return {'message': 'Климатическая зона обновлена'}

    @active_user_required
    def delete(self, id):
        zone = ClimateZone.query.get_or_404(id)
        db.session.delete(zone)
        db.session.commit()
        return {'message': 'Климатическая зона удалена'}