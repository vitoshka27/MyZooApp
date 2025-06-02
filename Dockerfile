FROM python:3.9-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt
RUN apt update && apt install -y sudo curl


EXPOSE 5000

ENV FLASK_APP=api/main_api.py
ENV FLASK_RUN_HOST=0.0.0.0

CMD ["flask", "run", "--host=0.0.0.0", "--port=5000"]