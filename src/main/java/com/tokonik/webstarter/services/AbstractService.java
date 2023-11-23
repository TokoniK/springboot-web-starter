package com.tokonik.webstarter.services;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.tokonik.webstarter.util.ServiceResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Metamodel;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractService<E extends Serializable>  {

    JpaRepository<E,Integer> repo;

    List<String> uniqueAttributes;

    @Autowired
    EntityManager entityManager;

    public ServiceResponse<List<E>> getAll() {

        List<E> result = repo.findAll();
        if(result.isEmpty()){
            return new ServiceResponse<>(result, HttpStatus.NOT_FOUND, null);
        }

        return new ServiceResponse<>(repo.findAll(), HttpStatus.OK, null);
    }

    public ServiceResponse<E> getById(Integer id){
        E result = repo.findById(id).orElse(null);
        if(result==null){
            return new ServiceResponse<>(result, HttpStatus.NOT_FOUND, null);
        }

        return new ServiceResponse<>(result, HttpStatus.OK, null);
    }

    public ServiceResponse<E> deleteById(Integer id){
        if(!repo.existsById(id))
            return new ServiceResponse<>(null, HttpStatus.NOT_FOUND, null);

        repo.deleteById(id);
        return new ServiceResponse<>(null, HttpStatus.OK, null);

    }

    public ServiceResponse<E> create(Object object){

        E obj;

        try{
            obj = (E) object;
        }catch (ClassCastException e){
            e.printStackTrace();
            return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, null);
        }

        List<E> duplicates = findDuplicates(obj);
        //Check for duplication and return conflict
        if(!duplicates.isEmpty())
            return new ServiceResponse<>(null, HttpStatus.CONFLICT, null);

        return new ServiceResponse<>(repo.save(obj), HttpStatus.CREATED, null);
    }

    public ServiceResponse<E> update(Object object){

        E obj;

        try{
//            obj = (E) object.getClass().cast(object);
            obj = (E) object;
        }catch (ClassCastException e){
            return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, null);
        }

        Integer id;
        id =getId(obj);
        if(id==null)
            return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, Arrays.asList("Request body is missing id value"));

        if(!repo.findById(getId(obj)).isPresent())
            return new ServiceResponse<>(null, HttpStatus.NOT_FOUND, null);

        List<E> duplicates = findDuplicates(obj).stream().filter(f-> !getId(f).equals(getId(obj))).collect(Collectors.toList());

        if(!duplicates.isEmpty())
            return new ServiceResponse<>(null, HttpStatus.CONFLICT, Arrays.asList("Update validates uniqueness constraints"));


        return new ServiceResponse<>(repo.save(obj), HttpStatus.OK, null);
    }

    public ServiceResponse<E> updateById(Object object, Integer id){

        E entity;

        try{
            entity = (E) object;
        }catch (ClassCastException e){
            return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, null);
        }

        Optional<E> o = repo.findById(id);
        if(!o.isPresent())
            return new ServiceResponse<>(null, HttpStatus.NOT_FOUND, null);

        Gson gson = new Gson();
        JsonObject element = gson.toJsonTree(object).getAsJsonObject();
        E obj = o.get();

        List<E> duplicates = findDuplicates(entity).stream().filter(f-> !getId(f).equals(id)).collect(Collectors.toList());

        if(!duplicates.isEmpty())
            return new ServiceResponse<>(null, HttpStatus.CONFLICT, Arrays.asList("Update violates uniqueness constraints"));


        Field idField = getIdField(obj);
        if(element.keySet().contains(idField.getName()))
            if(!id.equals(gson.fromJson(element.get(idField.getName()), Integer.class))) {
                return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, Arrays.asList("Path Id and Request Body Id mismatch"));

            }

        return new ServiceResponse<>(repo.save(entity), HttpStatus.OK, null);
    }

    public ServiceResponse<E> patchById(Object object, Integer id){

        E entity;

        try{
            entity = (E) object;
        }catch (ClassCastException e){
            return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, null);
        }

        Optional<E> o = repo.findById(id);
        if(!o.isPresent())
            return new ServiceResponse<>(null, HttpStatus.NOT_FOUND, null);


        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantDeserializer());
        gsonBuilder.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
//        gsonBuilder.registerTypeAdapter(Class.class, new ClassDeserializer());


        Gson gson = gsonBuilder.create();//new Gson();
        System.out.println(entity+" cxx");
        JsonObject element = gson.toJsonTree(entity).getAsJsonObject();
        E obj = o.get();
        Field idField = getIdField(obj);
        if(element.keySet().contains(idField.getName()))
            if(!id.equals(gson.fromJson(element.get(idField.getName()), Integer.class))) {
                return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, Arrays.asList("Path Id and Request Body Id mismatch"));

            }

        List<E> duplicates = findDuplicates(entity).stream().filter(f-> !getId(f).equals(id)).collect(Collectors.toList());

        if(!duplicates.isEmpty())
            return new ServiceResponse<>(null, HttpStatus.CONFLICT, Arrays.asList("Update violates uniqueness constraints"));


        Field[] allFields = obj.getClass().getDeclaredFields();

        //Loop through all fields, set non unique fields to null on example object
        for(String s : element.keySet()){
            if(s.equals(getIdField(obj).getName()))
                continue;

            Field field = Arrays.stream(allFields).filter(f -> f.getName().equals(s)).findFirst().get();
            ReflectionUtils.makeAccessible(field);
            try {

//                        System.out.println(field.getName()+" value=" + field.get(obj));
//                        System.out.println(gson.fromJson(element.get(s), field.getType()));
                field.set(obj, gson.fromJson(element.get(s), field.getType() ));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return new ServiceResponse<>(repo.save(obj), HttpStatus.OK, null);
    }











    /////////////////////



//    private ServiceResponse<E> create(E object){
//
//        List<E> duplicates = findDuplicates(object);
//        //Check for duplication and return conflict
//        if(!duplicates.isEmpty())
//            return new ServiceResponse<>(null, HttpStatus.CONFLICT, null);
//
//        return new ServiceResponse<>(repo.save(object), HttpStatus.CREATED, null);
//    }
//
//    private ServiceResponse<E> update(E object){
//
//        if(repo.findById(getId(object)).isEmpty())
//            return new ServiceResponse<>(null, HttpStatus.NOT_FOUND, null);
//
//        List<E> duplicates = findDuplicates(object).stream().filter(f-> !getId(f).equals(getId(object))).collect(Collectors.toList());
//
//        if(!duplicates.isEmpty())
//            return new ServiceResponse<>(null, HttpStatus.CONFLICT, Arrays.asList("Update validates uniqueness constraints"));
//
//
//        return new ServiceResponse<>(repo.save(object), HttpStatus.OK, null);
//    }
//
//    private ServiceResponse<E> updateById(E object, Integer id){
//
//
//        Optional<E> o = repo.findById(id);
//        if(o.isEmpty())
//            return new ServiceResponse<>(null, HttpStatus.NOT_FOUND, null);
//
//        Gson gson = new Gson();
//        JsonObject element = gson.toJsonTree(object).getAsJsonObject();
//        E obj = o.get();
//
//        List<E> duplicates = findDuplicates(object).stream().filter(f-> !getId(f).equals(id)).collect(Collectors.toList());
//
//        if(!duplicates.isEmpty())
//            return new ServiceResponse<>(null, HttpStatus.CONFLICT, Arrays.asList("Update validates uniqueness constraints"));
//
//
//        Field idField = getIdField(obj);
//        if(element.keySet().contains(idField.getName()))
//            if(!id.equals(gson.fromJson(element.get(idField.getName()), Integer.class))) {
//                return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, Arrays.asList("Path Id and Request Body Id mismatch"));
//
//            }
//
//        return new ServiceResponse<>(repo.save(object), HttpStatus.OK, null);
//    }
//
//    private ServiceResponse<E> patchById(E object, Integer id){
//
//        Optional<E> o = repo.findById(id);
//        if(o.isEmpty())
//            return new ServiceResponse<>(null, HttpStatus.NOT_FOUND, null);
//
//        Gson gson = new Gson();
//        JsonObject element = gson.toJsonTree(object).getAsJsonObject();
//        E obj = o.get();
//        Field idField = getIdField(obj);
//        if(element.keySet().contains(idField.getName()))
//            if(!id.equals(gson.fromJson(element.get(idField.getName()), Integer.class))) {
//                return new ServiceResponse<>(null, HttpStatus.BAD_REQUEST, Arrays.asList("Path Id and Request Body Id mismatch"));
//
//            }
//
//        List<E> duplicates = findDuplicates(object).stream().filter(f-> !getId(f).equals(id)).collect(Collectors.toList());
//
//        if(!duplicates.isEmpty())
//            return new ServiceResponse<>(null, HttpStatus.CONFLICT, Arrays.asList("Update validates uniqueness constraints"));
//
//
//        Field[] allFields = obj.getClass().getDeclaredFields();
//
//        //Loop through all fields, set non unique fields to null on example object
//        for(String s : element.keySet()){
//            if(s.equals(getIdField(obj).getName()))
//                continue;
//
//            Field field = Arrays.stream(allFields).filter(f -> f.getName().equals(s)).findFirst().get();
//            org.springframework.util.ReflectionUtils.makeAccessible(field);
//            try {
//
////                        System.out.println(field.getName()+" value=" + field.get(obj));
////                        System.out.println(gson.fromJson(element.get(s), field.getType()));
//                field.set(obj, gson.fromJson(element.get(s), field.getType() ));
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return new ServiceResponse<>(repo.save(obj), HttpStatus.OK, null);
//    }





    //////////

    public void setRepo(JpaRepository<E,Integer> jpaRepository){
        this.repo =jpaRepository;

    }

    public void setUniqueAttributes(List<String> uniqueAttributes) {
        this.uniqueAttributes = uniqueAttributes;
    }

    private Integer getId(E object){
        //Get field value annotated with @Id
        try {
            return (Integer) getIdField(object).get(object);
        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
            return null;
        }
    }


    private Field getIdField(E object){
        //Get field annotated with @Id
        Metamodel metamodel = entityManager.getMetamodel();
        String idFieldName = metamodel.entity(object.getClass())
                .getId(Integer.class)
                .getName();

        Class<?> clazz = object.getClass();
        Field field = ReflectionUtils.findField(clazz, idFieldName);
        assert field != null;
        ReflectionUtils.makeAccessible(field);

        return field;

    }

    private List<E> findDuplicates(E object){

//        if (uniqueAttributes==null)
//            return Arrays.asList();
//        if(uniqueAttributes.isEmpty())
//            return Arrays.asList();

        E exampleObject = (E) SerializationUtils.deserialize(SerializationUtils.serialize(object));
//        SerializationUtils.clone(object);
        if (uniqueAttributes==null)
            return repo.findAll(Example.of(exampleObject));
        if(uniqueAttributes.isEmpty())
            return repo.findAll(Example.of(exampleObject));

        /*//Get field annotated with @Id
        Metamodel metamodel = entityManager.getMetamodel();
        String idFieldName = metamodel.entity(object.getClass())
                .getId(Integer.class)
                .getName();

        Class<?> clazz = object.getClass();
        Field field = org.springframework.util.ReflectionUtils.findField(clazz, idFieldName);
        org.springframework.util.ReflectionUtils.makeAccessible(field);*/

        Field[] allFields = exampleObject.getClass().getDeclaredFields();
//        System.out.println(Arrays.stream(allFields).collect(Collectors.toList()).stream().map(Field::getName).collect(Collectors.toList())+" xx");

        //Loop through all fields, set non unique fields to null on example object
        for(Field field : allFields){
            if(!uniqueAttributes.contains(field.getName()) && !Modifier.isStatic(field.getModifiers())) {
                Field f = ReflectionUtils.findField(exampleObject.getClass(), field.getName());
                ReflectionUtils.makeAccessible(field);
                try {
//                    System.out.println("value=" + field.get(exampleObject));
                    field.set(exampleObject, null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return repo.findAll(Example.of(exampleObject));

    }
}

class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString(),
                DateTimeFormatter.ofPattern("d::MMM::uuuu HH::mm::ss").withLocale(Locale.ENGLISH));
    }
}

class InstantDeserializer implements JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

//        System.out.println(json);
//        System.out.println(json);
        JsonObject x = new JsonObject();
//        x.to
        if(json.isJsonObject()){
            String seconds =  json.getAsJsonObject().get("seconds").getAsString();
//            System.out.println("hello");
//            System.out.println(seconds);
            if(seconds!=null&&!seconds.isEmpty())
                return Instant.ofEpochSecond(Long.parseLong(seconds));
        }
        return Instant.parse(json.toString());

//        return Instant.parse(json.getAsString());
    }


}


//class ClassDeserializer implements JsonDeserializer<Class> {
//    @Override
//    public Class deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
//            throws JsonParseException {
//        System.out.println(json+" xcxxx");
//        JsonObject x = new JsonObject();
////        x.to
////        if(json.isJsonObject()){
////            String seconds =  json.getAsJsonObject().get("seconds").getAsString();
////            System.out.println("hello");
////            System.out.println(seconds);
////            if(seconds!=null&&!seconds.isEmpty())
////                return Instant.ofEpochSecond(new Long(seconds));
////        }
//        return null;//Instant.parse(json.toString());
//
////        return Instant.parse(json.getAsString());
//    }
//}

/**
 * This TypeAdapter unproxies Hibernate proxied objects, and serializes them
 * through the registered (or default) TypeAdapter of the base class.
 */

class HibernateProxyTypeAdapter extends TypeAdapter<HibernateProxy> {

    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            return (HibernateProxy.class.isAssignableFrom(type.getRawType()) ? (TypeAdapter<T>) new HibernateProxyTypeAdapter(gson) : null);
        }
    };
    private final Gson context;

    private HibernateProxyTypeAdapter(Gson context) {
        this.context = context;
    }

    @Override
    public HibernateProxy read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("Not supported");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void write(JsonWriter out, HibernateProxy value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        // Retrieve the original (not proxy) class
        Class<?> baseType = Hibernate.getClass(value);
        // Get the TypeAdapter of the original class, to delegate the serialization
        TypeAdapter delegate = context.getAdapter(TypeToken.get(baseType));
        // Get a filled instance of the original class
        Object unproxiedValue = ((HibernateProxy) value).getHibernateLazyInitializer()
                .getImplementation();
        // Serialize the value
        delegate.write(out, unproxiedValue);
    }
}
