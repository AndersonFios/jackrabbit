package com.meuapp;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.security.AccessControlList;

import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.jackrabbit.oak.Oak;
import org.apache.jackrabbit.oak.jcr.Jcr;

public class OakService {

    private static final String USERNAME = "admin";
    private static final char[] PASSWORD = "admin".toCharArray();

    public static void main(String[] args) throws RepositoryException {
        OakService service = new OakService();
        Session session = service.getSession();

        if (session != null) {
            System.out.println("Conexão bem-sucedida com o repositório Oak!");

            // aqui é possivel implementar códigos para estudo da ferramenta.

            session.logout();
        }
    }

    public Session getSession() {
        try {
            Jcr jcr = new Jcr(new Oak());
            Repository repository = jcr.createRepository();
            return repository.login(new SimpleCredentials(USERNAME, PASSWORD));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
