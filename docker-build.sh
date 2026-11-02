#!/bin/bash

echo "🗑️ Limpando containers antigos..."
sudo docker compose down -v

echo "🐳 Construindo imagem Docker (compila o projeto dentro dela)..."
sudo docker compose build

echo "🚀 Iniciando containers..."
sudo docker compose up -d

echo "⏳ Aguardando MySQL inicializar..."
sleep 10

echo "✅ Aplicação disponível em http://localhost:8080"
echo "📊 MySQL disponível na porta 3307"
echo "📚 Livros inseridos automaticamente!"
