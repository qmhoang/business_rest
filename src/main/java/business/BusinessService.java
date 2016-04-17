package business;

import business.model.Business;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.map.primitive.MutableIntObjectMap;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.factory.primitive.IntObjectMaps;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import static spark.Spark.*;

/**
 * Created by Quang-Minh on 4/16/2016.
 */
public class BusinessService {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
  private static final CsvMapper    CSV_MAPPER  = new CsvMapper();

  private static final String JSON_CONTENT       = "application/json";
  private static final int    NOT_FOUND_STATUS   = 404;
  private static final int    BAD_REQUEST_STATUS = 400;

  private static final ImmutableMap<String, String> NOT_FOUND_ERROR   = Maps.immutable.of("type", "error", "status", "404");
  private static final ImmutableMap<String, String> BAD_REQUEST_ERROR = Maps.immutable.of("type", "error", "status", "400");

  private static final int ENTRIES_PER_PAGE = 50;

  static {
    CSV_MAPPER.findAndRegisterModules();

    JSON_MAPPER.findAndRegisterModules();
    JSON_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
  }

  public static void main(String[] args) {
    MutableIntObjectMap<Business> businessMap  = readCsvData(args[0]);
    MutableList<Business>         businessList = businessMap.toList();

    port(5555);

    get("/business/:id", (req, res) -> {
      int id = Integer.parseInt(req.params(":id"));
      res.type(JSON_CONTENT);

      if (businessMap.containsKey(id)) {
        return JSON_MAPPER.writeValueAsString(businessMap.get(id));
      } else {
        res.status(NOT_FOUND_STATUS);
        return JSON_MAPPER.writeValueAsString(NOT_FOUND_ERROR.toMap()
                                                             .withKeyValue("message",
                                                                           MessageFormat.format("Could find find business with id: {0}", id)));
      }
    });

    get("/business/", (req, res) -> {
      res.type(JSON_CONTENT);
      MutableList<Business> list;

      if (req.queryParams().contains("index")) {
        int index = Integer.parseInt(req.queryParams("index"));
        res.header("index", Integer.toString(index));

        list = getSubListBusinesses(businessList, index);
      } else {
        res.header("index", "0");
        list = getSubListBusinesses(businessList, 0);
      }

      res.header("entries", Integer.toString(ENTRIES_PER_PAGE));
      return JSON_MAPPER.writeValueAsString(list);
    });

    exception(NumberFormatException.class, (e, req, res) -> {
      res.status(BAD_REQUEST_STATUS);
      try {
        res.body(JSON_MAPPER.writeValueAsString(BAD_REQUEST_ERROR.toMap()
                                                                 .withKeyValue("message",
                                                                               e.toString())));
      } catch (JsonProcessingException ex) {
        res.body("");
      }
    });
  }

  private static MutableList<Business> getSubListBusinesses(MutableList<Business> businessList, int index) {
    return businessList.subList(index, index + ENTRIES_PER_PAGE);
  }

  private static MutableIntObjectMap<Business> readCsvData(String arg) {
    MutableIntObjectMap<Business> businessMap = IntObjectMaps.mutable.of();

    try {
      MappingIterator<Business> iter = CSV_MAPPER.readerFor(Business.class)
                                                 .with(CsvSchema.emptySchema().withHeader())
                                                 .readValues(new File(arg));


      while (iter.hasNextValue()) {
        Business b = iter.next();
        businessMap.put(b.getId(), b);
      }


    } catch (IOException e) {
      e.printStackTrace();
    }

    return businessMap;
  }
}
