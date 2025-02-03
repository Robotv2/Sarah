package fr.maxlego08.sarah.requests;

import fr.maxlego08.sarah.DatabaseConfiguration;
import fr.maxlego08.sarah.DatabaseConnection;
import fr.maxlego08.sarah.SchemaBuilder;
import fr.maxlego08.sarah.database.Executor;
import fr.maxlego08.sarah.database.Schema;
import fr.maxlego08.sarah.logger.Logger;

import java.sql.SQLException;

public class ModifyRequest implements Executor {

    private final Schema schema;

    public ModifyRequest(Schema schema) {
        this.schema = schema;
    }

    @Override
    public int execute(DatabaseConnection databaseConnection, DatabaseConfiguration databaseConfiguration, Logger logger) {

        String tmpTableName = schema.getTableName() + "_tmp";
        Schema tmpSchema = SchemaBuilder.copy(tmpTableName, schema);

        try {
            tmpSchema.execute(databaseConnection, logger);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return -1;
        }

        Executor executor = new InsertAllRequest(schema, tmpTableName);
        executor.execute(databaseConnection, databaseConfiguration, logger);

        executor = new DropTableRequest(schema);
        executor.execute(databaseConnection, databaseConfiguration, logger);

        executor = new RenameExecutor(SchemaBuilder.rename(tmpTableName, schema.getTableName()));
        executor.execute(databaseConnection, databaseConfiguration, logger);

        return 0;
    }

}
