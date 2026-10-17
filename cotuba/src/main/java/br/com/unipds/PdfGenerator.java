package br.com.unipds;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

public class PdfGenerator {

  private final Path diretorioDosMD;
  private final Path arquivoDeSaida;

  public PdfGenerator(Path diretorioDosMD, Path arquivoDeSaida) {
    super();
    this.diretorioDosMD = diretorioDosMD;
    this.arquivoDeSaida = arquivoDeSaida;
  }

  public void gerarEbook() {
    try (var writer = new PdfWriter(Files.newOutputStream(arquivoDeSaida));
        var pdf = new PdfDocument(writer);
        var pdfDocument = new Document(pdf)) {

      // TODO: definir título e autor para o livro
      pdf.getDocumentInfo().setTitle("Livro");
      pdf.getDocumentInfo().setAuthor("Autor");

      // Lista os arquivos markdows em ordem alfabética
      ConversorDeMdParaHtml paraHtml = new ConversorDeMdParaHtml();
      List<String> htmls = paraHtml.converterParaHtml(diretorioDosMD);
      htmls.forEach(html -> {
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
      });
    } catch (Exception ex) {
      throw new IllegalStateException("Erro ao gerar PDF: " + arquivoDeSaida.toAbsolutePath(), ex);
    }
  }

}
