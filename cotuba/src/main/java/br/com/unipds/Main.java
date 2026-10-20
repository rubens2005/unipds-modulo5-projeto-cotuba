package br.com.unipds;

public class Main {

  public void main(String[] args) {
    int exitCode = new ApplicationService().executar(args);
    if (exitCode != 0) {
      System.exit(exitCode);
    }
  }

  public int executar(String[] args) {
    return new ApplicationService().executar(args);
  }

}
