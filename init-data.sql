USE matchbook;

-- Set the UTF-8 charset to support accents and special characters
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE TABLE books (
    id int AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100),
    genre VARCHAR(50) NOT NULL,
    publication_year INT,
    page_count INT,
    language VARCHAR(20) DEFAULT 'Português',
    synopsis TEXT,
    cover_url VARCHAR(500),
    owner_uid VARCHAR(128),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO books (title, author, publisher, genre, publication_year, page_count, language, synopsis, cover_url) VALUES
('1984', 'George Orwell', 'Companhia das Letras', 'Ficção Distópica', 1949, 408, 'Português', 'Uma sátira profética sobre um regime totalitário que controla todos os aspectos da vida, incluindo o pensamento.', 'https://www.lpm.com.br/livros/imagens/1984_pocket_corrigido_9786556662121_hd.jpg'),
('A Divina Comédia', 'Dante Alighieri', 'Ateliê Editorial', 'Clássico da Literatura', 1320, 560, 'Português', 'Poema épico que descreve a jornada de Dante pelo Inferno, Purgatório e Paraíso.', 'https://m.media-amazon.com/images/I/818Hap7wCcL._UF1000,1000_QL80_.jpg'),
('A Peregrina', 'John Bunyan', 'Camelot Editora', 'Literatura Religiosa', 1684, 128, 'Português', 'Narra a jornada de uma família cristã em busca da Cidade Celestial, encontrando figuras simbólicas pelo caminho.', 'https://m.media-amazon.com/images/I/91ETpgC6ljL._UF1000,1000_QL80_.jpg'),
('A Colônia Penal', 'Franz Kafka', 'Editora Garnier', 'Ficção Literária', 1919, 253, 'Português', 'Um viajante testemunha a execução de um soldado em uma colônia penal, levantando questões sobre justiça e poder.', 'https://m.media-amazon.com/images/I/81cedCa9MyL._UF1000,1000_QL80_.jpg'),
('A Promessa', 'Pnina Bat Zvi e Margie Wolfe', 'Callis', 'Literatura Infantojuvenil', 2018, 36, 'Português', 'A história de duas irmãs que fazem uma promessa de nunca se separarem após serem levadas para um campo de concentração.', 'https://m.media-amazon.com/images/I/81WOfP8UX9L.jpg'),
('Box – Six of Crows: Volume 1', 'Leigh Bardugo', 'Gutenberg', 'Fantasia', 2015, 824, 'Português', 'Um ladrão chamado Ketterdam reúne uma equipe de seis criminosos para um assalto aparentemente impossível.', 'https://prodimage.images-bn.com/pimages/9781250076960_p0_v11_s1200x630.jpg'),
('O Senhor dos Anéis: A Sociedade do Anel', 'J.R.R. Tolkien', 'HarperCollins Brasil', 'Fantasia Épica', 1954, 423, 'Português', 'Uma jornada épica começa quando um hobbit recebe a missão de destruir um anel de poder.', 'https://m.media-amazon.com/images/I/81hCVEC0ExL.jpg'),
('O Hobbit', 'J.R.R. Tolkien', 'HarperCollins Brasil', 'Fantasia', 1937, 310, 'Português', 'A aventura de Bilbo Bolseiro em busca de tesouros e encontros com criaturas fantásticas.', 'https://m.media-amazon.com/images/I/91M9xPIf10L.jpg'),
('Harry Potter e a Pedra Filosofal', 'J.K. Rowling', 'Rocco', 'Fantasia Juvenil', 1997, 223, 'Português', 'O início da saga do jovem bruxo Harry Potter na escola de magia de Hogwarts.', 'https://m.media-amazon.com/images/I/81ibfYk4qmL.jpg'),
('O Código Da Vinci', 'Dan Brown', 'Arqueiro', 'Suspense', 2003, 464, 'Português', 'Um simbologista embarca em uma corrida para decifrar segredos escondidos em obras de arte.', 'https://m.media-amazon.com/images/I/71marwX+lyL._UF1000,1000_QL80_.jpg'),
('Os Miseráveis', 'Victor Hugo', 'Martin Claret', 'Clássico', 1862, 1463, 'Português', 'Retrato épico da França do século XIX através da vida de Jean Valjean e outros personagens.', 'https://m.media-amazon.com/images/I/61jbdjR38IL._UF1000,1000_QL80_.jpg'),
('Orgulho e Preconceito', 'Jane Austen', 'Martins Fontes', 'Romance Clássico', 1813, 279, 'Português', 'A história de Elizabeth Bennet e as tensões sociais e amorosas na Inglaterra georgiana.', 'https://images.dlivros.org/Jane-Austen/orgulho-preconceito-jane-austen_large.webp'),
('O Pequeno Príncipe', 'Antoine de Saint-Exupéry', 'Agir', 'Infantil/Poético', 1943, 96, 'Português', 'Um piloto encontra um principezinho que conta lições poéticas sobre amizade e humanidade.', 'https://m.media-amazon.com/images/I/81TmOZIXvzL.jpg'),
('Cem Anos de Solidão', 'Gabriel García Márquez', 'Companhia das Letras', 'Realismo Mágico', 1967, 417, 'Português', 'A saga da família Buendía em Macondo, entre o fantástico e o trágico.', 'https://m.media-amazon.com/images/I/819vfB2BjyL._UF1000,1000_QL80_.jpg'),
('O Alquimista', 'Paulo Coelho', 'Rocco', 'Ficção Filosófica', 1988, 208, 'Português', 'A jornada de um jovem pastor em busca de seu destino e das lições do mundo.', 'https://m.media-amazon.com/images/I/81slUinjTlS._UF1000,1000_QL80_.jpg'),
('Memórias Póstumas de Brás Cubas', 'Machado de Assis', 'Garnier', 'Clássico Brasileiro', 1881, 160, 'Português', 'Narrativa irônica de um defunto-autor que revisita sua vida e critica a sociedade.', 'https://m.media-amazon.com/images/I/91GAAzBixYL._UF1000,1000_QL80_.jpg'),
('Dom Casmurro', 'Machado de Assis', 'Nova Aguilar', 'Romance Brasileiro', 1899, 256, 'Português', 'História de amor, ciúme e dúvida narrada por Bentinho sobre Capitu.', 'https://m.media-amazon.com/images/I/810IAPcQmoL._UF1000,1000_QL80_.jpg'),
('A Menina que Roubava Livros', 'Markus Zusak', 'Intrínseca', 'Ficção Histórica', 2005, 552, 'Português', 'Durante a Segunda Guerra, uma menina encontra consolo em livros e nas palavras que rouba.', 'https://m.media-amazon.com/images/I/61L+4OBhm-L._UF1000,1000_QL80_.jpg'),
('O Caçador de Pipas', 'Khaled Hosseini', 'Objetiva', 'Drama', 2003, 372, 'Português', 'Uma amizade marcada pela culpa e redenção ambientada no Afeganistão.', 'https://m.media-amazon.com/images/I/71vomSH-B2L._UF1000,1000_QL80_.jpg'),
('Sapiens: Uma Breve História da Humanidade', 'Yuval Noah Harari', 'Companhia das Letras', 'Ensaios', 2011, 498, 'Português', 'Panorama da história da espécie humana, de grupos de caçadores a sociedades complexas.', 'https://m.media-amazon.com/images/I/81BTkpMrLYL.jpg'),
('Homo Deus', 'Yuval Noah Harari', 'Companhia das Letras', 'Ensaios', 2015, 450, 'Português', 'Reflexões sobre o futuro da humanidade diante da tecnologia e ambições de imortalidade.', 'https://m.media-amazon.com/images/I/81ql3Ty2O2L.jpg'),
('O Poder do Hábito', 'Charles Duhigg', 'Objetiva', 'Autoajuda', 2012, 416, 'Português', 'Explora como hábitos funcionam e como podem ser mudados para melhorar a vida pessoal e profissional.', 'https://m.media-amazon.com/images/I/815iPX0SgkL.jpg'),
('Pai Rico, Pai Pobre', 'Robert T. Kiyosaki', 'Alta Books', 'Finanças Pessoais', 1997, 208, 'Português', 'Lições sobre dinheiro, investimentos e mentalidade financeira através de experiências pessoais.', 'https://m.media-amazon.com/images/I/71V4lNR2gKL.jpg'),
('O Morro dos Ventos Uivantes', 'Emily Brontë', 'Penguin-Companhia', 'Romance Gótico', 1847, 416, 'Português', 'Paixão intensa e vingança em uma história sombria nos charnecos ingleses.', 'https://m.media-amazon.com/images/I/71lqmkoeosL.jpg'),
('Drácula', 'Bram Stoker', 'Zahar', 'Gótico/Horror', 1897, 418, 'Português', 'Relatos epistolares que narram a luta contra o conde vampiro e sua ameaça à sociedade vitoriana.', 'https://m.media-amazon.com/images/I/61MgodE1s0L._UF1000,1000_QL80_.jpg'),
('Frankenstein', 'Mary Shelley', 'Zahar', 'Ficção Científica Clássica', 1818, 280, 'Português', 'A criação de um ser artificial e as consequências éticas e humanas de sua existência.', 'https://m.media-amazon.com/images/I/81xM7MtkypL._UF1000,1000_QL80_.jpg'),
('A Revolução dos Bichos', 'George Orwell', 'Companhia das Letras', 'Satira Política', 1945, 140, 'Português', 'Fábula satírica sobre poder e corrupção em uma fazenda onde os animais tomam o controle.', 'https://m.media-amazon.com/images/I/91BsZhxCRjL._UF1000,1000_QL80_.jpg'),
('O Iluminado', 'Stephen King', 'Suma', 'Terror', 1977, 447, 'Português', 'Um homem e sua família isolam-se em um hotel; forças sobrenaturais e loucura começam a emergir.', 'https://m.media-amazon.com/images/I/81Q+pJi4NjL.jpg'),
('It - A Coisa', 'Stephen King', 'Suma', 'Terror', 1986, 1138, 'Português', 'Um grupo de amigos enfrenta uma entidade assustadora que assume a forma dos seus piores medos.', 'https://m.media-amazon.com/images/I/91g9Dvtf+jL.jpg'),
('Beloved', 'Toni Morrison', 'Cosac Naify', 'Ficção', 1987, 324, 'Português', 'Uma ex-escrava é assombrada pelo passado e por uma presença que simboliza traumas não resolvidos.', 'https://m.media-amazon.com/images/I/41w7f8lrDiL._UF1000,1000_QL80_.jpg');
