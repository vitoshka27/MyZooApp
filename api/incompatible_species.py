from flask_restx import Namespace, Resource, fields
from .models import db, IncompatibleSpecies
from .decorators import active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('incompatible_species', description='Операции с несовместимыми видами')

incompatible_species_model = api.model('IncompatibleSpecies', {
    'species1_id': fields.Integer(required=True),
    'species2_id': fields.Integer(required=True),
})

@api.route('/')
class IncompatibleSpeciesList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = IncompatibleSpecies.query
        species1_id = request.args.get('species1_id', type=int)
        species2_id = request.args.get('species2_id', type=int)
        if species1_id:
            query = query.filter_by(species1_id=species1_id)
        if species2_id:
            query = query.filter_by(species2_id=species2_id)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in IncompatibleSpecies.__table__.columns:
            col = getattr(IncompatibleSpecies, order_by)
            if order_dir == 'desc':
                query = query.order_by(desc(col))
            else:
                query = query.order_by(col)
        page = request.args.get('page', 1, type=int)
        items = query.all()
        return {
            'total': len(items),
            'page': page,
            'data': [a.to_dict() for a in items]
        }

    @active_user_required
    @api.expect(incompatible_species_model)
    def post(self):
        data = api.payload
        record = IncompatibleSpecies(**data)
        db.session.add(record)
        db.session.commit()
        return {'message': 'Связь добавлена'}, 201

@api.route('/<int:species1_id>/<int:species2_id>')
class IncompatibleSpeciesResource(Resource):
    @active_user_required
    def get(self, species1_id, species2_id):
        record = IncompatibleSpecies.query.get_or_404((species1_id, species2_id))
        return record.to_dict()

    @active_user_required
    @api.expect(incompatible_species_model)
    def put(self, species1_id, species2_id):
        record = IncompatibleSpecies.query.get_or_404((species1_id, species2_id))
        data = api.payload
        for key, value in data.items():
            setattr(record, key, value)
        db.session.commit()
        return {'message': 'Связь обновлена'}

    @active_user_required
    def delete(self, species1_id, species2_id):
        record = IncompatibleSpecies.query.get_or_404((species1_id, species2_id))
        db.session.delete(record)
        db.session.commit()
        return {'message': 'Связь удалена'} 