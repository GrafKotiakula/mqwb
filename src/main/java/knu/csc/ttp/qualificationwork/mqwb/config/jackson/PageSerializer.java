package knu.csc.ttp.qualificationwork.mqwb.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.data.domain.Page;

import java.io.IOException;

@SuppressWarnings("rawtypes")
@JsonComponent
public class PageSerializer extends StdSerializer<Page> {

    public PageSerializer() {
        super(Page.class);
    }

    private void serializePagination(Page page, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeField("totalPages", page.getTotalPages(), gen);
        provider.defaultSerializeField("totalElements", page.getTotalElements(), gen);
        provider.defaultSerializeField("size", page.getSize(), gen);
        provider.defaultSerializeField("numberOfElements", page.getNumberOfElements(), gen);
        provider.defaultSerializeField("pageNumber", page.getNumber(), gen);
        provider.defaultSerializeField("first", page.isFirst(), gen);
        provider.defaultSerializeField("last", page.isLast(), gen);
        provider.defaultSerializeField("empty", page.isEmpty(), gen);
    }

    @Override
    public void serialize(Page page, JsonGenerator gen, SerializerProvider provider)throws IOException {
        gen.writeStartObject();

        provider.defaultSerializeField("content", page.getContent(), gen);

        gen.writeFieldName("pagination");
        gen.writeStartObject();
        serializePagination(page, gen, provider);
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
