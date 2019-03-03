package ru.ifmo.web.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.ifmo.web.database.dao.AstartesDAO;
import ru.ifmo.web.database.entity.Astartes;
import ru.ifmo.web.service.exception.AstartesServiceException;
import ru.ifmo.web.service.exception.AstartesServiceFault;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@WebService(serviceName = "astartes", targetNamespace = "astartes_namespace")
@AllArgsConstructor
@NoArgsConstructor
public class AstartesService {
    private AstartesDAO astartesDAO;

    @WebMethod
    public List<Astartes> findAll() throws AstartesServiceException {
        try {
            return astartesDAO.findAll();
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new AstartesServiceException(message, e, new AstartesServiceFault(message));
        }
    }

    @WebMethod
    public List<Astartes> findWithFilters(@WebParam(name = "id") Long id, @WebParam(name = "name") String name,
                                          @WebParam(name = "title") String title, @WebParam(name = "position") String position,
                                          @WebParam(name = "planet") String planet, @WebParam(name = "birthdate") Date birthdate) throws AstartesServiceException {
        try {
            return astartesDAO.findWithFilters(id, name, title, position, planet, birthdate);
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new AstartesServiceException(message, e, new AstartesServiceFault(message));
        }
    }

    @WebMethod
    public int update(@WebParam(name = "id") Long id, @WebParam(name = "name") String name,
                      @WebParam(name = "title") String title, @WebParam(name = "position") String position,
                      @WebParam(name = "planet") String planet, @WebParam(name = "birthdate") Date birthdate) throws AstartesServiceException {
        try {
            int count = astartesDAO.update(id, name, title, position, planet, birthdate);
            if (count == 0) {
                throw new AstartesServiceException("Запись с id=" + id + " не существует", new AstartesServiceFault("Запись с id=" + id + " не существует"));
            }
            return count;
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new AstartesServiceException(message, e, new AstartesServiceFault(message));
        }
    }

    @WebMethod
    public int delete(@WebParam(name = "id") Long id) throws AstartesServiceException {
        try {
            int count = astartesDAO.delete(id);
            if (count == 0) {
                throw new AstartesServiceException("Запись с id=" + id + " не существует", new AstartesServiceFault("Запись с id=" + id + " не существует"));
            }
            return count;
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new AstartesServiceException(message, e, new AstartesServiceFault(message));
        }
    }

    @WebMethod
    public Long create(@WebParam(name = "name") String name,
                       @WebParam(name = "title") String title, @WebParam(name = "position") String position,
                       @WebParam(name = "planet") String planet, @WebParam(name = "birthdate") Date birthdate) throws AstartesServiceException {
        try {
            return astartesDAO.create(name, title, position, planet, birthdate);
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new AstartesServiceException(message, e, new AstartesServiceFault(message));
        }
    }

    @WebMethod
    public Long createWithObject(@WebParam(name = "astartes") AstartesRequestObject astartes) throws AstartesServiceException {
        try {
            return astartesDAO.create(astartes.getName(), astartes.getTitle(), astartes.getPosition(), astartes.getPlanet(), astartes.getBirthdate());
        } catch (SQLException e) {
            String message = "SQL exception: " + e.getMessage() + ". State: " + e.getSQLState();
            throw new AstartesServiceException(message, e, new AstartesServiceFault(message));
        }
    }
}
