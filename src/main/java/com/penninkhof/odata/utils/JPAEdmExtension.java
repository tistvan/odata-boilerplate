package com.penninkhof.odata.utils;

import org.apache.olingo.odata2.api.edm.provider.*;
import org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmSchemaView;
import org.apache.olingo.odata2.jpa.processor.core.model.JPAEdmMappingImpl;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class JPAEdmExtension implements org.apache.olingo.odata2.jpa.processor.api.model.JPAEdmExtension {

	public static final String MAPPING_MODEL = "odata-mapping.xml";
	public static final String SAP_NAMESPACE = "http://www.sap.com/Protocols/SAPData";
	public static final String SAP_PREFIX = "sap";
	public static final String LABEL = "label";
	
	@Override
	public void extendWithOperation(JPAEdmSchemaView view) {
	}

	@Override
	public void extendJPAEdmSchema(JPAEdmSchemaView view) {
		ResourceBundle i18n = ODataContextUtil.getResourceBundle("i18n");
		final Schema edmSchema = view.getEdmSchema();
		
		for (EntityType entityType : edmSchema.getEntityTypes()) {
			for (Property property : entityType.getProperties()) {
				String label = null;
				if (i18n != null) { try { label = i18n.getString(entityType.getName() + "." + property.getName()); } catch (Exception e) {} }
				List<AnnotationAttribute> annotationAttributeList = new ArrayList<AnnotationAttribute>();
				if (label != null) {
					annotationAttributeList.add(new AnnotationAttribute()
							.setNamespace(SAP_NAMESPACE)
							.setPrefix(SAP_PREFIX)
							.setName(LABEL).setText(label));
				}
				annotationAttributeList.addAll(getSapPropertyAnnotations(entityType, property));
				property.setAnnotationAttributes(annotationAttributeList); 
			}
		}
		
		addSmartAnnotations(edmSchema);
	}


	@SuppressWarnings("unchecked")
	private Collection<AnnotationAttribute> getSapPropertyAnnotations(EntityType entityType, Property property) {
		List<AnnotationAttribute> result = new ArrayList<AnnotationAttribute>();
		/*for (Field field : ((JPAEdmMappingImpl)entityType.getMapping()).getJPAType().getDeclaredFields()) {
			if (field.getName().equals(((JPAEdmMappingImpl) property.getMapping()).getInternalName())) {
				if (field.getAnnotation(Sap.class) != null) {
					InvocationHandler handler = Proxy.getInvocationHandler(field.getAnnotation(Sap.class));
					Field f = null;
					try {
				        f = handler.getClass().getDeclaredField("memberValues");
				    } catch (NoSuchFieldException | SecurityException e) {
				        continue;
				    }
					f.setAccessible(true);
					Map<String, Object> memberValues = null;
				    try {
				        memberValues = (Map<String, Object>) f.get(handler);
				    } catch (IllegalArgumentException | IllegalAccessException e) {
				    	continue;
				    }
				    for (Entry<String, Object> memberValue : memberValues.entrySet()) {
						result.add(new AnnotationAttribute()
								.setNamespace(SAP_NAMESPACE)
								.setPrefix(SAP_PREFIX)
								.setName(memberValue.getKey())
								.setText(String.valueOf(memberValue.getValue())));				
					}
				}
			}
		}*/

		result.add(new AnnotationAttribute()
				.setNamespace(SAP_NAMESPACE)
				.setPrefix(SAP_PREFIX)
				.setName("sortable")
				.setText("true"));
		result.add(new AnnotationAttribute()
				.setNamespace(SAP_NAMESPACE)
				.setPrefix(SAP_PREFIX)
				.setName("filterable")
				.setText("true"));
		result.add(new AnnotationAttribute()
				.setNamespace(SAP_NAMESPACE)
				.setPrefix(SAP_PREFIX)
				.setName("creatable")
				.setText("true"));
		result.add(new AnnotationAttribute()
				.setNamespace(SAP_NAMESPACE)
				.setPrefix(SAP_PREFIX)
				.setName("updatable")
				.setText("true"));
		return result;
	}
	
	@SuppressWarnings("serial")
	private void addSmartAnnotations(final Schema edmSchema) {
		List<AnnotationElement> schemaAnnotations = new ArrayList<AnnotationElement>();
		edmSchema.setAnnotationElements(schemaAnnotations);
		for (final EntityContainer container : edmSchema.getEntityContainers()) {
			for (final EntitySet entitySet : container.getEntitySets()) {
				schemaAnnotations.add(new AnnotationElement()
					.setName("Annotations")
					.setAttributes(
						new ArrayList<AnnotationAttribute>() {{ add(new AnnotationAttribute()
							.setName("Target")
							.setNamespace("http://docs.oasis-open.org/odata/ns/edm")
							.setText(entitySet.getEntityType().toString()));
					}})
					.setChildElements(new ArrayList<AnnotationElement>() {{ add(new AnnotationElement()
						.setName("Annotation")
						.setAttributes(
							new ArrayList<AnnotationAttribute>() {{ add(new AnnotationAttribute()
								.setName("Term")
								.setText("com.sap.vocabularies.UI.v1.LineItem"));
						}})
						.setChildElements(new ArrayList<AnnotationElement>() {{ add(new AnnotationElement()
							.setName("Collection")
							.setChildElements(new ArrayList<AnnotationElement>() {{
								for (final Field field : getFieldsOfEntitySet()) {
										add(new AnnotationElement()
											.setName("Record")
											.setAttributes(
												new ArrayList<AnnotationAttribute>() {{ add(new AnnotationAttribute()
													.setName("Type")
													.setText("com.sap.vocabularies.UI.v1.DataField"));
											}})
											.setChildElements(new ArrayList<AnnotationElement>() {{
												add(new AnnotationElement()
													.setName("PropertyValue")
													.setAttributes(
														new ArrayList<AnnotationAttribute>() {{ 
															add(new AnnotationAttribute()
																.setName("Path")
																.setText(getEdmNameOfField()));
															add(new AnnotationAttribute()
																.setName("Property")
																.setText("Value"));
												}
												private String getEdmNameOfField() {
													for (EntityType entityType : edmSchema.getEntityTypes()) {
														if (field.getDeclaringClass().equals(((JPAEdmMappingImpl)entityType.getMapping()).getJPAType())) {
															for (Property property : entityType.getProperties()) {
																if (field.getName().equals(((JPAEdmMappingImpl) property.getMapping()).getInternalName())) {
																	return property.getName();
																}
															}
														}
													}
													return null;
												}}));
											}}));

							}}
							private Field[] getFieldsOfEntitySet() {
								for (EntityType entityType : edmSchema.getEntityTypes()) {
									if (entityType.getName().equals(entitySet.getEntityType().getName())) {
										return ((JPAEdmMappingImpl) entityType.getMapping()).getJPAType().getDeclaredFields();
									}
								}
								return null;
							}})
						);}})
					);}})
				);
			}
		}
	}

	@Override
	public InputStream getJPAEdmMappingModelStream() {
		return JPAEdmExtension.class.getClassLoader().getResourceAsStream(MAPPING_MODEL);
	}

}