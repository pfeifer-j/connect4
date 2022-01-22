package connect4;

import connect4.controller.Controller;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Shell {

  public static void main(String[] args) {
    Controller controller = new Controller();
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in));
    controller.execute(reader);
  }
}
