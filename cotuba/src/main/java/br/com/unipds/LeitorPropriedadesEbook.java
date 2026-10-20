package br.com.unipds;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class LeitorPropriedadesEbook {

  private Properties properties;

  public void ler(Ebook ebook) {
    properties = new Properties();
    Path diretorioDosMD = ebook.getDiretorioDosMD();
    Path arquivoProperties = diretorioDosMD.resolve("ebook.properties");
    if (!Files.exists(arquivoProperties)) {
      throw new IllegalStateException("Arquivo ebook.properties não encontrado em " + diretorioDosMD);
    }
    try (BufferedReader reader = Files.newBufferedReader(arquivoProperties, StandardCharsets.UTF_8)) {
      properties.load(reader);
    } catch (IOException ex) {
      throw new IllegalStateException("Erro ao ler o arquivo " + arquivoProperties, ex);
    }
    ebook.setTitulo(getPropriedade("cotuba.ebook.titulo"));
    ebook.setAutor(getPropriedade("cotuba.ebook.autor"));
  }

  private String getPropriedade(String propriedade) {
    String conteudo = properties.getProperty(propriedade);
    if (conteudo == null || conteudo.isBlank()) {
      throw new IllegalArgumentException("Propriedade inválida: " + propriedade);
    }
    return conteudo.trim();
  }

}
