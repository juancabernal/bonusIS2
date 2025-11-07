package co.edu.uco.ucochallenge.crosscutting.secrets;

public interface SecretProvider {
  String get(String name);
}
