package ca.uwaterloo;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufFactory;
import com.fasterxml.jackson.dataformat.protobuf.schema.NativeProtobufSchema;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import com.fasterxml.jackson.dataformat.protobuf.schemagen.ProtobufSchemaGenerator;
import lombok.*;
import org.junit.Test;

public class ConvertToProto
{
    @Test
    public void convertToProto()
        throws JsonMappingException
    {
        ObjectMapper mapper = new ObjectMapper(new ProtobufFactory());
        ProtobufSchemaGenerator gen = new ProtobufSchemaGenerator();
        mapper.acceptJsonFormatVisitor(Person.class, gen);
        ProtobufSchema schemaWrapper = gen.getGeneratedSchema();
        NativeProtobufSchema nativeProtobufSchema = schemaWrapper.getSource();

        String asProtofile = nativeProtobufSchema.toString();
        System.out.println(asProtofile);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @ToString
    public class Person
    {
        String name;
        String address;
        int age;
    }

}
