package br.com.unipds;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class RenderizadorMarkdown {

  private final RepositorioMarkdown markdownRepository;

  public RenderizadorMarkdown() {
    super();
    markdownRepository = new RepositorioMarkdown();
  }

  public void renderizar(Ebook ebook) {
    List<Path> arquivosMD = markdownRepository.listarArquivos(ebook.getDiretorioDosMD());
    arquivosMD.forEach(arquivoMD -> {
      Capitulo capitulo = ebook.novoCapitulo();
      capitulo.setArquivoMD(arquivoMD);
      Parser parser = Parser.builder().build();
      Node document = null;
      try {
        String markdown = Files.readString(arquivoMD);
        capitulo.setMarkdown(markdown);
        document = parser.parse(markdown);
        document.accept(new AbstractVisitor() {
          @Override
          public void visit(Heading heading) {
            if (heading.getLevel() == 1) {
              String titulo = ((Text) heading.getFirstChild()).getLiteral();
              capitulo.setTitulo(titulo);
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
        capitulo.setHtml(html);
      } catch (Exception ex) {
        throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
      }
    });
  }

}
