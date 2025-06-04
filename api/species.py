from flask_restx import Namespace, Resource, fields
from .models import db, Species
from .decorators import active_user_required
from flask import request
from sqlalchemy import desc

api = Namespace('species', description='Операции с видами животных')

species_model = api.model('Species', {
    'id': fields.Integer(readonly=True),
    'type_name': fields.String(required=True),
    'need_warm': fields.String(required=True),
})

@api.route('/')
class SpeciesList(Resource):
    @active_user_required
    @api.param('X-Fields', 'Fields to include in response', _in='header')
    def get(self):
        query = Species.query
        type_name = request.args.get('type_name')
        need_warm = request.args.get('need_warm')
        if type_name:
            query = query.filter(Species.type_name.ilike(f'%{type_name}%'))
        if need_warm:
            query = query.filter_by(need_warm=need_warm)
        order_by = request.args.get('order_by')
        order_dir = request.args.get('order_dir', 'asc')
        if isinstance(order_by, str) and order_by in Species.__table__.columns:
            col = getattr(Species, order_by)
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
    @api.expect(species_model)
    def post(self):
        data = api.payload
        species = Species(**data)
        db.session.add(species)
        db.session.commit()
        return {'message': 'Вид добавлен', 'id': species.id}, 201

@api.route('/<int:id>')
class SpeciesResource(Resource):
    @active_user_required
    def get(self, id):
        species = Species.query.get_or_404(id)
        return species.to_dict()

    @active_user_required
    @api.expect(species_model)
    def put(self, id):
        species = Species.query.get_or_404(id)
        for key, value in api.payload.items():
            setattr(species, key, value)
        db.session.commit()
        return {'message': 'Вид обновлен'}

    @active_user_required
    def delete(self, id):
        species = Species.query.get_or_404(id)
        db.session.delete(species)
        db.session.commit()
        return {'message': 'Вид удален'} 