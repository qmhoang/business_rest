package business;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static spark.Spark.*;

/**
 * Created by qhoang on 5/9/16.
 */
public class UserService {
  private static MutableMap<String, String> usersToToken = Maps.mutable.of();

  private static       ImmutableMap<String, String> users         = Maps.immutable.of("admin", "admin")
                                                                                  .newWithKeyValue("superuser", "root");
  private static final ObjectMapper                 JSON_MAPPER   = new ObjectMapper();
  private static final String                       JSON_CONTENT  = "application/json";
  private static       int                          DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
  private static JwtBuilder jwtBuilder;


  public static void main(String[] args) {
    port(5556);

    Key key = MacProvider.generateKey();
    jwtBuilder = Jwts.builder();


    get("/auth", ((request, response) -> {
      response.type(JSON_CONTENT);
      String authorization_hash = request.headers("Authorization");
      if (authorization_hash.contains("Basic")) {
        authorization_hash = authorization_hash.substring(6);
      } else {
        halt(401);
      }

      String s[] = new String(Base64.getDecoder().decode(authorization_hash)).split(":");
      String user = s[0];
      String pass = s[1];

      if (!users.containsKey(user) ||
          !users.get(user).contentEquals(pass)) {
        halt(401);
        return ""; // should never hit
      } else {
        Date iat = new Date();
        String token = jwtBuilder
            .setSubject(user)
            .signWith(SignatureAlgorithm.HS512, key)
            .setIssuedAt(iat)
            .setExpiration(new Date(iat.getTime() + DAY_IN_MILLIS))
            .compact();

        usersToToken.put(user, token);

        return JSON_MAPPER.writeValueAsString(Maps.mutable.of("token", token));
      }
    }));

    get("/verify", (request, response) -> {
      String token = request.headers("Authorization");

      String subject = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();

      if (usersToToken.containsKey(subject) && usersToToken.get(subject).contentEquals(token)) {
      } else {
        halt(401);
      }

      return "";
    });

    exception(com.fasterxml.jackson.core.JsonParseException.class, (e, req, res) -> {
      halt(401);
    });
  }
}
