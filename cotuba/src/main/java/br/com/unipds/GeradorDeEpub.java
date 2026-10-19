package br.com.unipds;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

public class GeradorDeEpub {

  public void gerar(Ebook ebook) {
    Path arquivoDeSaida = ebook.getArquivoDeSaida();
    try {
      var epub = new Book();
      epub.getMetadata().addTitle(ebook.getTitulo());
      epub.getMetadata().addAuthor(new Author(ebook.getAutor()));

      boolean[] ehPrimeiroCapitulo = { true };

      for (Capitulo capitulo : ebook.getCapitulos()) {
        String html = capitulo.getHtml();
        String epubHtml = """
              <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                  <title>TTTTTitulo</title>
                </head>
                <body>
                  %s
                </body>
              </html>
            """.formatted(html);
        var chapter = new Resource(epubHtml.getBytes(), MediatypeService.XHTML);
        epub.addSection(capitulo.getTitulo(), chapter);

        if (ehPrimeiroCapitulo[0]) {
          epub.getGuide().addReference(new GuideReference(chapter, "text", "Start Reading"));
          ehPrimeiroCapitulo[0] = false;
        }
      }
      try {
        var epubWriter = new EpubWriter();
        epubWriter.write(epub, Files.newOutputStream(arquivoDeSaida));
      } catch (IOException ex) {
        throw new IllegalStateException("Erro ao criar arquivo EPUB: " + arquivoDeSaida.toAbsolutePath(), ex);
      }
    } catch (Exception ex) {
      throw new IllegalStateException("Erro ao gerar EPUB: " + arquivoDeSaida.toAbsolutePath(), ex);
    }
  }

}
