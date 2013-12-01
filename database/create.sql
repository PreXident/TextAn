/*
Deployment script for TEXTANAL
*/

GO
SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;


GO
:setvar DatabaseName "TEXTANAL"
:setvar DefaultDataPath "c:\Program Files\Microsoft SQL Server\MSSQL10.SQLEXPRESS\MSSQL\DATA\"
:setvar DefaultLogPath "c:\Program Files\Microsoft SQL Server\MSSQL10.SQLEXPRESS\MSSQL\DATA\"

GO
:on error exit
GO
USE [master]
GO
IF (DB_ID(N'$(DatabaseName)') IS NOT NULL
    AND DATABASEPROPERTYEX(N'$(DatabaseName)','Status') <> N'ONLINE')
BEGIN
    RAISERROR(N'The state of the target database, %s, is not set to ONLINE. To deploy to this database, its state must be set to ONLINE.', 16, 127,N'$(DatabaseName)') WITH NOWAIT
    RETURN
END

GO

IF NOT EXISTS (SELECT 1 FROM [master].[dbo].[sysdatabases] WHERE [name] = N'$(DatabaseName)')
BEGIN
    RAISERROR(N'You cannot deploy this update script to target VENDA-PC\SQLEXPRESS. The database for which this script was built, TEXTANAL, does not exist on this server.', 16, 127) WITH NOWAIT
    RETURN
END

GO

IF (@@servername != 'VENDA-PC\SQLEXPRESS')
BEGIN
    RAISERROR(N'The server name in the build script %s does not match the name of the target server %s. Verify whether your database project settings are correct and whether your build script is up to date.', 16, 127,N'VENDA-PC\SQLEXPRESS',@@servername) WITH NOWAIT
    RETURN
END

GO

IF CAST(DATABASEPROPERTY(N'$(DatabaseName)','IsReadOnly') as bit) = 1
BEGIN
    RAISERROR(N'You cannot deploy this update script because the database for which it was built, %s , is set to READ_ONLY.', 16, 127, N'$(DatabaseName)') WITH NOWAIT
    RETURN
END

GO
USE [$(DatabaseName)]
GO
/*
 Pre-Deployment Script Template							
--------------------------------------------------------------------------------------
 This file contains SQL statements that will be executed before the build script.	
 Use SQLCMD syntax to include a file in the pre-deployment script.			
 Example:      :r .\myfile.sql								
 Use SQLCMD syntax to reference a variable in the pre-deployment script.		
 Example:      :setvar TableName MyTable							
               SELECT * FROM [$(TableName)]					
--------------------------------------------------------------------------------------
*/

GO
PRINT N'Creating [dbo].[Alias]...';


GO
CREATE TABLE [dbo].[Alias] (
    [id_alias]  INT          NOT NULL,
    [id_object] INT          NOT NULL,
    [alias]     NVARCHAR (1) NOT NULL,
    PRIMARY KEY CLUSTERED ([id_alias] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);


GO
PRINT N'Creating [dbo].[AliasOccurrence]...';


GO
CREATE TABLE [dbo].[AliasOccurrence] (
    [id_alias]    INT NOT NULL,
    [id_document] INT NOT NULL,
    [position]    INT NOT NULL,
    CONSTRAINT [PK_ALIAS_OCCURRENCE] PRIMARY KEY CLUSTERED ([id_alias] ASC, [id_document] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);


GO
PRINT N'Creating [dbo].[Document]...';


GO
CREATE TABLE [dbo].[Document] (
    [id_document] INT          NOT NULL,
    [added]       DATE         NULL,
    [processed]   DATE         NULL,
    [text]        NVARCHAR (1) NOT NULL,
    PRIMARY KEY CLUSTERED ([id_document] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);


GO
PRINT N'Creating [dbo].[Object]...';


GO
CREATE TABLE [dbo].[Object] (
    [id_object]      INT          NOT NULL,
    [id_object_type] INT          NOT NULL,
    [data]           NVARCHAR (1) NULL,
    PRIMARY KEY CLUSTERED ([id_object] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);


GO
PRINT N'Creating [dbo].[ObjectType]...';


GO
CREATE TABLE [dbo].[ObjectType] (
    [id_object_type] INT          NOT NULL,
    [name]           NVARCHAR (1) NULL,
    PRIMARY KEY CLUSTERED ([id_object_type] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF),
    UNIQUE NONCLUSTERED ([name] ASC) WITH (ALLOW_PAGE_LOCKS = ON, ALLOW_ROW_LOCKS = ON, PAD_INDEX = OFF, IGNORE_DUP_KEY = OFF, STATISTICS_NORECOMPUTE = OFF)
);


GO
PRINT N'Creating On column: added...';


GO
ALTER TABLE [dbo].[Document]
    ADD DEFAULT GETDATE() FOR [added];


GO
PRINT N'Creating On column: id_object...';


GO
ALTER TABLE [dbo].[Alias] WITH NOCHECK
    ADD FOREIGN KEY ([id_object]) REFERENCES [dbo].[Object] ([id_object]) ON DELETE NO ACTION ON UPDATE NO ACTION;


GO
PRINT N'Creating On column: id_alias...';


GO
ALTER TABLE [dbo].[AliasOccurrence] WITH NOCHECK
    ADD FOREIGN KEY ([id_alias]) REFERENCES [dbo].[Alias] ([id_alias]) ON DELETE NO ACTION ON UPDATE NO ACTION;


GO
PRINT N'Creating On column: id_document...';


GO
ALTER TABLE [dbo].[AliasOccurrence] WITH NOCHECK
    ADD FOREIGN KEY ([id_document]) REFERENCES [dbo].[Document] ([id_document]) ON DELETE NO ACTION ON UPDATE NO ACTION;


GO
PRINT N'Creating On column: id_object_type...';


GO
ALTER TABLE [dbo].[Object] WITH NOCHECK
    ADD FOREIGN KEY ([id_object_type]) REFERENCES [dbo].[ObjectType] ([id_object_type]) ON DELETE NO ACTION ON UPDATE NO ACTION;


GO
/*
Post-Deployment Script Template							
--------------------------------------------------------------------------------------
 This file contains SQL statements that will be appended to the build script.		
 Use SQLCMD syntax to include a file in the post-deployment script.			
 Example:      :r .\myfile.sql								
 Use SQLCMD syntax to reference a variable in the post-deployment script.		
 Example:      :setvar TableName MyTable							
               SELECT * FROM [$(TableName)]					
--------------------------------------------------------------------------------------
*/

GO
PRINT N'Checking existing data against newly created constraints';


GO
USE [$(DatabaseName)];


GO

GO
CREATE TABLE [#__checkStatus] (
    [Table]      NVARCHAR (270),
    [Constraint] NVARCHAR (270),
    [Where]      NVARCHAR (MAX)
);

SET NOCOUNT ON;


GO
INSERT INTO [#__checkStatus]
EXECUTE (N'DBCC CHECKCONSTRAINTS (N''[dbo].[Alias]'')
    WITH NO_INFOMSGS');

IF @@ROWCOUNT > 0
    BEGIN
        DROP TABLE [#__checkStatus];
        RAISERROR (N'An error occured while verifying constraints on table [dbo].[Alias]', 16, 127);
    END


GO
INSERT INTO [#__checkStatus]
EXECUTE (N'DBCC CHECKCONSTRAINTS (N''[dbo].[AliasOccurrence]'')
    WITH NO_INFOMSGS');

IF @@ROWCOUNT > 0
    BEGIN
        DROP TABLE [#__checkStatus];
        RAISERROR (N'An error occured while verifying constraints on table [dbo].[AliasOccurrence]', 16, 127);
    END


GO
INSERT INTO [#__checkStatus]
EXECUTE (N'DBCC CHECKCONSTRAINTS (N''[dbo].[Object]'')
    WITH NO_INFOMSGS');

IF @@ROWCOUNT > 0
    BEGIN
        DROP TABLE [#__checkStatus];
        RAISERROR (N'An error occured while verifying constraints on table [dbo].[Object]', 16, 127);
    END


GO
SET NOCOUNT OFF;

DROP TABLE [#__checkStatus];


GO
