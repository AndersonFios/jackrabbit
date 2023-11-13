package com.meuapp;

import javax.jcr.*;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.NodeTypeTemplate;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
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


            NamespaceRegistry namespaceRegistry = session.getWorkspace().getNamespaceRegistry();
            namespaceRegistry.registerNamespace("my", "http://example.com/my_namespace");
            session.save();



            // Criar um template para o novo tipo de nó
            NodeTypeManager nodeTypeManager = session.getWorkspace().getNodeTypeManager();
            NodeTypeTemplate nodeTypeTemplate = nodeTypeManager.createNodeTypeTemplate();
            nodeTypeTemplate.setName("my:book");
            nodeTypeTemplate.setDeclaredSuperTypeNames(new String[] {"nt:unstructured"});

            // Adicionar propriedades ao tipo de nó
            ValueFactory valueFactory = session.getValueFactory();

            PropertyDefinitionTemplate titleProp = nodeTypeManager.createPropertyDefinitionTemplate();
            titleProp.setName("my:title");
            titleProp.setRequiredType(javax.jcr.PropertyType.STRING);
            nodeTypeTemplate.getPropertyDefinitionTemplates().add(titleProp);

            PropertyDefinitionTemplate authorProp = nodeTypeManager.createPropertyDefinitionTemplate();
            authorProp.setName("my:author");
            authorProp.setRequiredType(javax.jcr.PropertyType.STRING);
            nodeTypeTemplate.getPropertyDefinitionTemplates().add(authorProp);

            PropertyDefinitionTemplate isbnProp = nodeTypeManager.createPropertyDefinitionTemplate();
            isbnProp.setName("my:isbn");
            isbnProp.setRequiredType(javax.jcr.PropertyType.STRING);
            isbnProp.setMandatory(true);
            nodeTypeTemplate.getPropertyDefinitionTemplates().add(isbnProp);

            nodeTypeTemplate.setQueryable(true);

            // Registrar o tipo de nó
            nodeTypeManager.registerNodeType(nodeTypeTemplate, true);
            session.save();




            nodeTypeTemplate.setName("my:ebook");
            nodeTypeTemplate.setDeclaredSuperTypeNames(new String[] {"my:book"}); // Herda de my:book

            // Adicionar propriedades ao tipo de nó
            PropertyDefinitionTemplate fileFormatProp = nodeTypeManager.createPropertyDefinitionTemplate();
            fileFormatProp.setName("my:fileFormat");
            fileFormatProp.setRequiredType(javax.jcr.PropertyType.STRING);
            nodeTypeTemplate.getPropertyDefinitionTemplates().add(fileFormatProp);

            // Registrar o tipo de nó
            nodeTypeManager.registerNodeType(nodeTypeTemplate, true);
            session.save();




            Node rootNode = session.getRootNode();
            Node ebookNode = rootNode.addNode("sampleEbook", "my:ebook");
            ebookNode.setProperty("my:title", "Sample eBook");
            ebookNode.setProperty("my:author", "John Doe");
            ebookNode.setProperty("my:isbn", "1234567890");
            ebookNode.setProperty("my:fileFormat", "epub");
            session.save();



            rootNode = session.getRootNode();
            Node ebookNodeSearch = rootNode.getNode("sampleEbook");

            String namespaceURI = session.getNamespaceURI("my");




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

    public void setPermissionsForMyNamespace(String groupName) throws Exception {
        Node myDocumentsNode = createMyNamespaceDocumentsNode();

        JackrabbitAccessControlList acl = AccessControlUtils.getAccessControlList(getSession(), myDocumentsNode.getPath());
        if (acl != null) {
            Privilege[] privileges = {
                    getSession().getAccessControlManager().privilegeFromName(Privilege.JCR_READ),
                    getSession().getAccessControlManager().privilegeFromName(Privilege.JCR_WRITE)
            };
            acl.addAccessControlEntry(AccessControlUtils.getPrincipal(getSession(), groupName), privileges);

            getSession().getAccessControlManager().setPolicy(myDocumentsNode.getPath(), acl);
            getSession().save();
        }
    }

    public Node createMyNamespaceDocumentsNode() throws Exception {
        Node rootNode = getSession().getRootNode();
        if (!rootNode.hasNode("my:documents")) {
            return rootNode.addNode("my:documents", "nt:folder");
        } else {
            return rootNode.getNode("my:documents");
        }
    }
}
