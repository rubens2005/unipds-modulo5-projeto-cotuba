# Análise dos problemas

- O arquivo tem só uma classe que faz tudo e mistura um monte de conceitos.
- O arquivo tem só um método enorme que faz tudo: parse dos parâmetros da linha de comando, condições de que tipo de arquivo gerar e geração de cada tipo de arquivo.
  - Já separei em dois métodos, um para gerar PDF e outro para gerar Epub.
- O processo de listar os arquivos markdowns está DUPLICADO na geração dos arquivos PDF e Epub.
  - O percorrimento nos arquivos também: arquivosMD.forEach(arquivoMD -> {.....
- Adicionar um novo tipo de saída, como HTML, não é fácil 
- Existem TODOs para adicionar o título e o autor do livro.
- Existem TODOs para tratar mais níveis de títulos, como nível 2 e nível três.
- O markdown é transformado primeiro em HTML, para depois em PDF ou Epub. A transformação para HTML está duplicada nos dois formatos.


# Propostas de soluções

- Criar uma classe para conter os parâmetros de linha de comando.
  - Analisa os parâmetros
  - Validada 

- Criar uma interface e classes para cada tipo de arquivo.
  - Talvez criar uma estratégia para cada tipo de arquivo

- Criar um arquivo de configuração para indicar os tipos de arquivos aceitáveis para a geração de saídas.


- No processo de geração criar um template method que contém o algoritmo gerar de geração de arquivos, delegando para as classes específicos os método especializados de acordo com o tipo do arquivo a ser gerado.