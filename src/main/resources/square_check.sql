CREATE OR ALTER PROCEDURE square_check
@lat REAL, @long REAL, @milesDelta REAL, @min INT, @max INT,
@count INT OUTPUT
AS
BEGIN

	PRINT 'starting square check lat: '+convert(varchar(10),@lat)+', long: '+convert(varchar(10),@long)+', milesDelta: '+convert(varchar(10),@milesDelta)+
			 ', min: '+convert(varchar(10),@min)+', max: '+convert(varchar(10),@max);

	DECLARE @latDelta REAL = @milesDelta / 69,
			@longDelta REAL = @milesDelta / (69 * COS(RADIANS(@lat)));

	SELECT
        @count = count(*)
    FROM 
        dbo.doors
	WHERE
		latitude BETWEEN (@lat - @latdelta) and (@lat + @latdelta)
		AND
		longitude BETWEEN (@long - @longdelta) and (@long + @longdelta);
	
	PRINT 'count is: ' + convert(varchar(10),@count);

	RETURN
END;