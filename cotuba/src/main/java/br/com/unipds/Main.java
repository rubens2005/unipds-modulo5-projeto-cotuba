package br.com.unipds;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.properties.AreaBreakType;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.GuideReference;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import nl.siegmann.epublib.service.MediatypeService;

public class Main {

  void main(String[] args) {
    int exitCode = executar(args);
    if (exitCode != 0) {
      System.exit(exitCode);
    }
  }

  int executar(String[] args) {
    // Definição e validação dos argumentos da linha de comando
    CliArgs cliArgs = new CliArgs();
    var ajuda = new HelpFormatter();
    boolean modoVerboso = true;
    try {
      cliArgs.parse(args);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      ajuda.printHelp("cotuba", cliArgs.getOptions());
      return 1;
    } catch (IOException e) {
      System.err.println(e.getMessage());
      if (modoVerboso) {
        System.err.println();
        e.printStackTrace();
      }
      return 1;
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      if (modoVerboso) {
        System.err.println();
        e.printStackTrace();
      }
      return 1;
    }

    // Extração e utilização dos argumentos da linha de comando.
    Path diretorioDosMD = cliArgs.getDiretorioDosMD();
    FormatoEbook formato = cliArgs.getFormato();
    Path arquivoDeSaida = cliArgs.getArquivoDeSaida();
    modoVerboso = cliArgs.isModoVerboso();

    try {
      if (FormatoEbook.PDF.equals(formato)) {
        gerarPDF(diretorioDosMD, arquivoDeSaida);
      } else if (FormatoEbook.EPUB.equals(formato)) {
        gerarEpub(diretorioDosMD, arquivoDeSaida);
      } else {
        throw new IllegalArgumentException("Formato do ebook inválido: " + formato);
      }
      System.out.println("Arquivo gerado com sucesso: " + arquivoDeSaida);
      return 0;
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      if (modoVerboso) {
        System.err.println();
        ex.printStackTrace();
      }
      return 1;
    }
  }

  private void gerarPDF(Path diretorioDosMD, Path arquivoDeSaida) {
    try (var writer = new PdfWriter(Files.newOutputStream(arquivoDeSaida));
        var pdf = new PdfDocument(writer);
        var pdfDocument = new Document(pdf)) {

      // TODO: definir título e autor para o livro
      pdf.getDocumentInfo().setTitle("Livro");
      pdf.getDocumentInfo().setAuthor("Autor");

      // Lista os arquivos markdows em ordem alfabética
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
      try (Stream<Path> streamMDs = Files.list(diretorioDosMD)) {
        List<Path> arquivosMD = streamMDs.filter(matcher::matches).sorted().toList();
        if (arquivosMD.isEmpty()) {
          throw new IllegalStateException(
              "Não foram encontrados capítulos (arquivos .md) no diretório: " + diretorioDosMD.toAbsolutePath());
        }

        arquivosMD.forEach(arquivoMD -> {
          Parser parser = Parser.builder().build();
          Node document = null;
          try {
            document = parser.parseReader(Files.newBufferedReader(arquivoMD));
            document.accept(new AbstractVisitor() {
              @Override
              public void visit(Heading heading) {
                if (heading.getLevel() == 1) {
                  // capítulo
                  String tituloDoCapitulo = ((Text) heading.getFirstChild()).getLiteral();
                  // TODO: usar título do capítulo
                } else if (heading.getLevel() == 2) {
                  // seção
                } else if (heading.getLevel() == 3) {
                  // título
                }
              }

            });
          } catch (Exception ex) {
            throw new IllegalStateException("Erro ao fazer parse do arquivo " + arquivoMD, ex);
          }

          try {
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String html = renderer.render(document);

            List<IElement> convertToElements = HtmlConverter.convertToElements(html);

            if (pdf.getNumberOfPages() == 0) {
              pdf.addNewPage();
            }
            PdfOutline rootOutline = pdf.getOutlines(false);
            if (rootOutline == null) {
              pdf.initializeOutlines();
              rootOutline = pdf.getOutlines(false);
            }

            // TODO: usar título do capítulo
            PdfOutline chapterOutline = rootOutline.addOutline("Capítulo");
            chapterOutline.addDestination(PdfExplicitDestination.createFit(pdf.getLastPage()));

            for (IElement element : convertToElements) {
              pdfDocument.add((IBlockElement) element);
            }
            // TODO: não adicionar página depois do último capítulo
            pdfDocument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

          } catch (Exception ex) {
            throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
          }

        });
      } catch (IOException ex) {
        throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + diretorioDosMD.toAbsolutePath(),
            ex);
      }

    } catch (Exception ex) {
      throw new IllegalStateException("Erro ao gerar PDF: " + arquivoDeSaida.toAbsolutePath(), ex);
    }
  }

  private void gerarEpub(Path diretorioDosMD, Path arquivoDeSaida) {
    try {
      var epub = new Book();

      // TODO: definir título e autor para o livro
      epub.getMetadata().addTitle("Livro");
      epub.getMetadata().addAuthor(new Author("Autor"));

      boolean[] ehPrimeiroCapitulo = { true };

      // Lista os arquivos markdows em ordem alfabética
      PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");
      try (Stream<Path> streamMDs = Files.list(diretorioDosMD)) {
        List<Path> arquivosMD = streamMDs.filter(matcher::matches).sorted().toList();

        if (arquivosMD.isEmpty()) {
          throw new IllegalStateException(
              "Não foram encontrados capítulos (arquivos .md) no diretório: " + diretorioDosMD.toAbsolutePath());
        }

        arquivosMD.forEach(arquivoMD -> {
          Parser parser = Parser.builder().build();
          Node document = null;
          try {
            document = parser.parseReader(Files.newBufferedReader(arquivoMD));
            document.accept(new AbstractVisitor() {
              @Override
              public void visit(Heading heading) {
                if (heading.getLevel() == 1) {
                  // capítulo
                  String tituloDoCapitulo = ((Text) heading.getFirstChild()).getLiteral();
                  // TODO: usar título do capítulo
                } else if (heading.getLevel() == 2) {
                  // seção
                } else if (heading.getLevel() == 3) {
                  // título
                }
              }

            });
          } catch (Exception ex) {
            throw new IllegalStateException("Erro ao fazer parse do arquivo " + arquivoMD, ex);
          }

          try {
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            String html = renderer.render(document);

            // TODO: usar título do capítulo
            String epubHtml = """
                  <html xmlns="http://www.w3.org/1999/xhtml">
                    <head>
                      <title>Capítulo</title>
                    </head>
                    <body>
                      %s
                    </body>
                  </html>
                """.formatted(html);
            var chapter = new Resource(epubHtml.getBytes(), MediatypeService.XHTML);
            epub.addSection("Capítulo", chapter);

            if (ehPrimeiroCapitulo[0]) {
              epub.getGuide().addReference(new GuideReference(chapter, "text", "Start Reading"));
              ehPrimeiroCapitulo[0] = false;
            }

          } catch (Exception ex) {
            throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
          }
        });
      } catch (IOException ex) {
        throw new IllegalStateException("Erro tentando encontrar arquivos .md em " + diretorioDosMD.toAbsolutePath(),
            ex);
      }

      var epubWriter = new EpubWriter();

      try {
        epubWriter.write(epub, Files.newOutputStream(arquivoDeSaida));
      } catch (IOException ex) {
        throw new IllegalStateException("Erro ao criar arquivo EPUB: " + arquivoDeSaida.toAbsolutePath(), ex);
      }

    } catch (Exception ex) {
      throw new IllegalStateException("Erro ao gerar EPUB: " + arquivoDeSaida.toAbsolutePath(), ex);
    }
  }

}
