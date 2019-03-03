package ru.ifmo.web.client;

import lombok.SneakyThrows;
import org.apache.juddi.api_v3.AccessPointType;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;
import ru.ifmo.web.client.AstartesRequestObject;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

public class Client {
    private static JUDDIClient juddiClient;
    private static AstartesService service;

    public static void main(String... args) throws IOException {
//        URL url = new URL("http://localhost:8081/astartes?wsdl");
        Astartes_Service astartesService = new Astartes_Service();
        service = astartesService.getAstartesServicePort();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter JUDDI username");
        String username = bufferedReader.readLine().trim();
        System.out.println("Enter JUDDI user password");
        String password = bufferedReader.readLine().trim();
        juddiClient = new JUDDIClient("META-INF/uddi.xml");
        juddiClient.authenticate(username, password);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int currentState = 11;

        while (true) {
            switch (currentState) {
                case 0:
                    System.out.println("\nВыберите один из пунктов:");
                    System.out.println("1. Вывести всех астартес");
                    System.out.println("2. Применить фильтры");
                    System.out.println("3. Создать");
                    System.out.println("4. Изменить");
                    System.out.println("5. Удалить");
                    System.out.println("6. Вывести все бизнесы");
                    System.out.println("7. Зарегистрировать бизнес");
                    System.out.println("8. Зарегистрировать сервис");
                    System.out.println("9. Найти и использовать сервис");
                    System.out.println("10. Выйти");
                    currentState = readState(currentState, reader);
                    break;
                case 1:
                    System.out.println("Найдено:");
                    try {
                        service.findAll().stream().map(Client::astartesToString).forEach(System.out::println);
                    } catch (AstartesServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 2:
                    System.out.println("\nЧтобы не применять фильтр, оставьте значение пустым");
                    System.out.println("id:");
                    Long id = readLong(reader);
                    System.out.println("name:");
                    String name = readString(reader);
                    System.out.println("title:");
                    String title = readString(reader);
                    System.out.println("position:");
                    String position = readString(reader);
                    System.out.println("planet:");
                    String planet = readString(reader);
                    System.out.println("birthdate(yyyy-mm-dd):");
                    XMLGregorianCalendar birthdate = readDate(reader);
                    try {
                        System.out.println("Найдено:");
                        service.findWithFilters(id, name, title, position, planet, birthdate).stream().map(Client::astartesToString).forEach(System.out::println);
                    } catch (AstartesServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 3:
                    System.out.println("\nЗаполните все поля");
                    String createName;
                    do {
                        System.out.println("name:");
                        createName = readString(reader);
                    } while (createName == null);
                    String createTitle;
                    do {
                        System.out.println("title:");
                        createTitle = readString(reader);
                    } while (createTitle == null);
                    String createPosition;
                    do {
                        System.out.println("position:");
                        createPosition = readString(reader);
                    } while (createPosition == null);
                    String createPlanet;
                    do {
                        System.out.println("planet:");
                        createPlanet = readString(reader);
                    } while (createPlanet == null);
                    XMLGregorianCalendar createBirthdate;
                    do {
                        System.out.println("birthdate(yyyy-mm-dd):");
                        createBirthdate = readDate(reader);
                    } while (createBirthdate == null);
                    Long createdId;
                    try {
                        AstartesRequestObject requestObject = new AstartesRequestObject();
                        requestObject.setId(null);
                        requestObject.setName(createName);
                        requestObject.setPlanet(createPlanet);
                        requestObject.setBirthdate(createBirthdate);
                        requestObject.setPosition(createPosition);
                        requestObject.setTitle(createTitle);
                        createdId = service.createWithObject(requestObject);
                        System.out.println("ID новой записи: " + createdId);
                    } catch (AstartesServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 4:
                    Long updateId;
                    do {
                        System.out.println("id изменяемой записи (0 для отмены операции):");
                        updateId = readLong(reader);
                    } while (updateId == null);

                    if (updateId == 0L) {
                        currentState = 0;
                        break;
                    }
                    System.out.println("name:");
                    String updateName = readString(reader);
                    System.out.println("title:");
                    String updateTitle = readString(reader);
                    System.out.println("position:");
                    String updatePosition = readString(reader);
                    System.out.println("planet:");
                    String updatePlanet = readString(reader);
                    System.out.println("birthdate(yyyy-mm-dd):");
                    XMLGregorianCalendar updateBirthdate = readDate(reader);
                    int updateRes;
                    try {
                        updateRes = service.update(updateId, updateName, updateTitle, updatePosition, updatePlanet, updateBirthdate);
                        System.out.println("Изменено " + updateRes + " строк");
                    } catch (AstartesServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 5:
                    Long deleteId;
                    do {
                        System.out.println("id удаляемой записи (0 для отмены операции):");
                        deleteId = readLong(reader);
                    } while (deleteId == null);
                    if (deleteId == 0L) {
                        currentState = 0;
                        break;
                    }
                    int deleteRes;
                    try {
                        deleteRes = service.delete(deleteId);
                        System.out.println("Удалено " + deleteRes + " строк");
                    } catch (AstartesServiceException e) {
                        System.out.println(e.getFaultInfo().getMessage());
                    } finally {
                        currentState = 0;
                    }
                    break;
                case 6:
                    listBusinesses(null);
                    currentState = 0;
                    break;
                case 7:
                    System.out.println("Введите имя бизнеса");
                    String bn = readString(reader);
                    if (bn != null) {
                        createBusiness(bn);
                    }
                    currentState = 0;
                    break;
                case 8:
                    listBusinesses(null);
                    String bk;
                    do {
                        System.out.println("Введите ключ бизнеса");
                        bk = readString(reader);
                    } while (bk == null);

                    String sn;
                    do {
                        System.out.println("Введите имя сервиса");
                        sn = readString(reader);
                    } while (sn == null);

                    String surl;
                    do {
                        System.out.println("Введите ссылку на wsdl");
                        surl = readString(reader);
                    } while (surl == null);
                    createService(bk, sn, surl);
                    currentState = 0;
                    break;
                case 9:
                    System.out.println("Введите имя сервиса для поиска");
                    String fsn = readString(reader);
                    filterServices(fsn);
                    System.out.println("Введите ключ сервиса");
                    String key = readString(reader);
                    if (key != null) {
                        useService(key);
                    }
                    currentState = 0;
                    break;
                case 10:
                    return;
                case 11:
                    int state = 0;
                    boolean br = false;
                    while (!br) {
                        switch (state) {
                            case 0:
                                System.out.println("\nВыберите один из пунктов:");
                                System.out.println("1. Вывести все бизнесы");
                                System.out.println("2. Зарегистрировать бизнес");
                                System.out.println("3. Зарегистрировать сервис");
                                System.out.println("4. Найти и использовать сервис");
                                System.out.println("5. Выйти");
                                state = readState(currentState, reader);
                                break;
                            case 1:
                                listBusinesses(null);
                                state=0;
                                break;
                            case 2:
                                System.out.println("Введите имя бизнеса");
                                String bnn = readString(reader);
                                if (bnn != null) {
                                    createBusiness(bnn);
                                }
                                state = 0;
                                break;
                            case 3:
                                listBusinesses(null);
                                String bbk;
                                do {
                                    System.out.println("Введите ключ бизнеса");
                                    bbk = readString(reader);
                                } while (bbk == null);

                                String ssn;
                                do {
                                    System.out.println("Введите имя сервиса");
                                    ssn = readString(reader);
                                } while (ssn == null);

                                String ssurl;
                                do {
                                    System.out.println("Введите ссылку на wsdl");
                                    ssurl = readString(reader);
                                } while (ssurl == null);
                                createService(bbk, ssn, ssurl);
                                state = 0;
                                break;
                            case 4:
                                System.out.println("Введите имя сервиса для поиска");
                                String ffsn = readString(reader);
                                filterServices(ffsn);
                                System.out.println("Введите ключ сервиса");
                                String kkey = readString(reader);
                                if (kkey != null) {
                                    useService(kkey);
                                }
                                currentState = 0;
                                br=true;
                                break;
                            case 5:
                                return;
                            default:
                                state = 0;
                                break;

                        }
                    }
                    break;
                default:
                    currentState = 0;
                    break;
            }
        }
    }


    @SneakyThrows
    private static void useService(String serviceKey) {

        ServiceDetail serviceDetail = juddiClient.getService(serviceKey.trim());
        if (serviceDetail == null || serviceDetail.getBusinessService() == null || serviceDetail.getBusinessService().isEmpty()) {
            System.out.printf("Can not find service by key '%s'\b", serviceKey);
            return;
        }
        List<BusinessService> services = serviceDetail.getBusinessService();
        BusinessService businessService = services.get(0);
        BindingTemplates bindingTemplates = businessService.getBindingTemplates();
        if (bindingTemplates == null || bindingTemplates.getBindingTemplate().isEmpty()) {
            System.out.printf("No binding template found for service '%s' '%s'\n", serviceKey, businessService.getBusinessKey());
            return;
        }
        for (BindingTemplate bindingTemplate : bindingTemplates.getBindingTemplate()) {
            AccessPoint accessPoint = bindingTemplate.getAccessPoint();
            if (accessPoint.getUseType().equals(AccessPointType.END_POINT.toString())) {
                String value = accessPoint.getValue();
                System.out.printf("Use endpoint '%s'\n", value);
                changeEndpointUrl(value);
                return;
            }
        }
        System.out.printf("No endpoint found for service '%s'\n", serviceKey);
    }

    @SneakyThrows
    private static void createService(String businessKey, String serviceName, String wsdlUrl) {
        List<ServiceDetail> serviceDetails = juddiClient.publishUrl(businessKey.trim(), serviceName.trim(), wsdlUrl.trim());
        System.out.printf("Services published from wsdl %s\n", wsdlUrl);
        JUDDIUtil.printServicesInfo(serviceDetails.stream()
                .map(ServiceDetail::getBusinessService)
                .flatMap(List::stream)
                .collect(Collectors.toList())
        );
    }

    @SneakyThrows
    public static void createBusiness(String businessName) {
        businessName = businessName.trim();
        BusinessDetail business = juddiClient.createBusiness(businessName);
        System.out.println("New business was created");
        for (BusinessEntity businessEntity : business.getBusinessEntity()) {
            System.out.printf("Key: '%s'\n", businessEntity.getBusinessKey());
            System.out.printf("Name: '%s'\n", businessEntity.getName().stream().map(Name::getValue).collect(Collectors.joining(" ")));
        }
    }

    public static void changeEndpointUrl(String endpointUrl) {
        ((BindingProvider) service).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl.trim());
    }


    @SneakyThrows
    private static void filterServices(String filterArg) {
        List<BusinessService> services = juddiClient.getServices(filterArg);
        JUDDIUtil.printServicesInfo(services);
    }

    @SneakyThrows
    private static void listBusinesses(Void ignored) {
        JUDDIUtil.printBusinessInfo(juddiClient.getBusinessList().getBusinessInfos());
    }

    private static String readString(BufferedReader reader) throws IOException {
        String trim = reader.readLine().trim();
        if (trim.isEmpty()) {
            return null;
        }
        return trim;
    }

    private static XMLGregorianCalendar readDate(BufferedReader reader) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date rd = sdf.parse(reader.readLine());

            GregorianCalendar c = new GregorianCalendar();

            if (rd != null) {
                c.setTime(rd);
                return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } else {
                return null;
            }
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static Date readNotXMLDate(BufferedReader reader) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            return sdf.parse(reader.readLine());

        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static Long readLong(BufferedReader reader) {
        try {
            return Long.parseLong(reader.readLine());
        } catch (java.lang.Exception e) {
            return null;
        }
    }

    private static int readState(int current, BufferedReader reader) {
        try {
            return Integer.parseInt(reader.readLine());
        } catch (java.lang.Exception e) {
            return current;
        }
    }

    private static String astartesToString(Astartes astartes) {
        return "Astartes(" +
                "id=" + astartes.getId() +
                ", name=" + astartes.getName() +
                ", title=" + astartes.getTitle() +
                ", position=" + astartes.getPosition() +
                ", planet=" + astartes.getPlanet() +
                ", birthdate=" + astartes.getBirthdate() +
                ")";
    }

}
