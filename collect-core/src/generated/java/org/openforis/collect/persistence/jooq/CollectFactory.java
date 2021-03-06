/**
 * This class is generated by jOOQ
 */
package org.openforis.collect.persistence.jooq;

/**
 * This class is generated by jOOQ.
 *
 * A Factory for specific use with the <code>collect</code> schema.
 * <p>
 * This Factory will not render the <code>collect</code> schema's schema name 
 * in rendered SQL (assuming that you use it as the default schema on your 
 * connection!). Use the more generic {@link org.jooq.util.postgres.PostgresFactory} 
 * or the {@link org.jooq.impl.Factory} instead, if you want to fully qualify 
 * tables, routines, etc.
 */
@javax.annotation.Generated(value    = {"http://www.jooq.org", "2.5.0"},
                            comments = "This class is generated by jOOQ")
public class CollectFactory extends org.jooq.util.postgres.PostgresFactory {

	private static final long serialVersionUID = -210961119;

	/**
	 * Create a factory with a connection
	 *
	 * @param connection The connection to use with objects created from this factory
	 */
	public CollectFactory(java.sql.Connection connection) {
		super(connection);

		initDefaultSchema();
	}

	/**
	 * Create a factory with a data source
	 *
	 * @param dataSource The data source to use with objects created from this factory
	 */
	public CollectFactory(javax.sql.DataSource dataSource) {
		super(dataSource);

		initDefaultSchema();
	}

	/**
	 * Create a factory with a connection and some settings
	 *
	 * @param connection The connection to use with objects created from this factory
	 * @param settings The settings to apply to objects created from this factory
	 */
	public CollectFactory(java.sql.Connection connection, org.jooq.conf.Settings settings) {
		super(connection, settings);

		initDefaultSchema();
	}

	/**
	 * Create a factory with a data source and some settings
	 *
	 * @param dataSource The data source to use with objects created from this factory
	 * @param settings The settings to apply to objects created from this factory
	 */
	public CollectFactory(javax.sql.DataSource dataSource, org.jooq.conf.Settings settings) {
		super(dataSource, settings);

		initDefaultSchema();
	}

	/**
	 * Initialise the render mapping's default schema.
	 * <p>
	 * For convenience, this schema-specific factory should override any pre-existing setting
	 */
	private final void initDefaultSchema() {
		org.jooq.conf.SettingsTools.getRenderMapping(getSettings()).setDefaultSchema(org.openforis.collect.persistence.jooq.Collect.COLLECT.getName());
	}
}
