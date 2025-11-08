package co.edu.uco.ucochallenge.crosscutting.secret;

public interface SecretProvider {
  String get(String name);
}
