-- Moves the genre of the 30 seed books to the genre list of the app's screens
-- (`genres` in the front's src/styles/tokens.ts), so the favorite genre counts in the recommendation.
-- init-data.sql only runs on a brand-new database volume; this script updates a database that already exists.
-- Only seed books (no owner_uid) are touched. Safe to run more than once.
-- Run it with:
--   docker exec -i matchbook-mysql sh -c 'mysql --default-character-set=utf8mb4 -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE"' < update-seed-genres.sql

SET NAMES utf8mb4;

UPDATE books SET genre = 'Distopia' WHERE title = '1984' AND owner_uid IS NULL; -- was Ficção Distópica
UPDATE books SET genre = 'Outros' WHERE title = 'A Divina Comédia' AND owner_uid IS NULL; -- was Clássico da Literatura
UPDATE books SET genre = 'Outros' WHERE title = 'A Peregrina' AND owner_uid IS NULL; -- was Literatura Religiosa
UPDATE books SET genre = 'Drama' WHERE title = 'A Colônia Penal' AND owner_uid IS NULL; -- was Ficção Literária
UPDATE books SET genre = 'Infantil' WHERE title = 'A Promessa' AND owner_uid IS NULL; -- was Literatura Infantojuvenil
UPDATE books SET genre = 'Fantasia' WHERE title = 'O Senhor dos Anéis: A Sociedade do Anel' AND owner_uid IS NULL; -- was Fantasia Épica
UPDATE books SET genre = 'Fantasia' WHERE title = 'Harry Potter e a Pedra Filosofal' AND owner_uid IS NULL; -- was Fantasia Juvenil
UPDATE books SET genre = 'Drama' WHERE title = 'Os Miseráveis' AND owner_uid IS NULL; -- was Clássico
UPDATE books SET genre = 'Romance' WHERE title = 'Orgulho e Preconceito' AND owner_uid IS NULL; -- was Romance Clássico
UPDATE books SET genre = 'Infantil' WHERE title = 'O Pequeno Príncipe' AND owner_uid IS NULL; -- was Infantil/Poético
UPDATE books SET genre = 'Drama' WHERE title = 'Cem Anos de Solidão' AND owner_uid IS NULL; -- was Realismo Mágico
UPDATE books SET genre = 'Aventura' WHERE title = 'O Alquimista' AND owner_uid IS NULL; -- was Ficção Filosófica
UPDATE books SET genre = 'Comédia' WHERE title = 'Memórias Póstumas de Brás Cubas' AND owner_uid IS NULL; -- was Clássico Brasileiro
UPDATE books SET genre = 'Romance' WHERE title = 'Dom Casmurro' AND owner_uid IS NULL; -- was Romance Brasileiro
UPDATE books SET genre = 'História' WHERE title = 'A Menina que Roubava Livros' AND owner_uid IS NULL; -- was Ficção Histórica
UPDATE books SET genre = 'História' WHERE title = 'Sapiens: Uma Breve História da Humanidade' AND owner_uid IS NULL; -- was Ensaios
UPDATE books SET genre = 'História' WHERE title = 'Homo Deus' AND owner_uid IS NULL; -- was Ensaios
UPDATE books SET genre = 'Autoajuda' WHERE title = 'Pai Rico, Pai Pobre' AND owner_uid IS NULL; -- was Finanças Pessoais
UPDATE books SET genre = 'Romance' WHERE title = 'O Morro dos Ventos Uivantes' AND owner_uid IS NULL; -- was Romance Gótico
UPDATE books SET genre = 'Terror' WHERE title = 'Drácula' AND owner_uid IS NULL; -- was Gótico/Horror
UPDATE books SET genre = 'Ficção Científica' WHERE title = 'Frankenstein' AND owner_uid IS NULL; -- was Ficção Científica Clássica
UPDATE books SET genre = 'Distopia' WHERE title = 'A Revolução dos Bichos' AND owner_uid IS NULL; -- was Satira Política
UPDATE books SET genre = 'Drama' WHERE title = 'Beloved' AND owner_uid IS NULL; -- was Ficção
