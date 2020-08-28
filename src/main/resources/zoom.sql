CREATE OR ALTER PROCEDURE zoom 
@lat REAL, @long REAL, @min INT, @max INT, @starting_mile_delta REAL

AS
BEGIN

	PRINT 'starting zoom with lat: '+convert(varchar(10),@lat)+', long: '+convert(varchar(10),@long) +', min: '+convert(varchar(10),@min)+', max: '+convert(varchar(10),@max);

	DECLARE @currentCount INT, @repetitions INT = 0, @multiplier REAL = 0, @miles_delta REAL = @starting_mile_delta;

    exec square_check @lat, @long, @miles_delta, @min, @max, @count = @currentCount OUTPUT;
	
	PRINT 'query executed count: ' + convert(varchar(10),@currentCount) + ' in zoom: 1 ';
	
	WHILE (@currentCount < @min or @currentCount > @max) and (@repetitions <= 5)
		BEGIN
			SET @repetitions += 1;
			IF @multiplier = 0
				IF @currentCount > @max
					SET @multiplier = .5 
				ELSE 
					SET @multiplier = 2 
			ELSE
				IF (@multiplier > 1 AND @currentCount > @max) or (@multiplier < 1 AND @currentCount < @min)
					BEGIN
						PRINT 'to many or to little result, stopping looping on current zoom';
						BREAK;
					END
			
			SET	@miles_delta = @miles_delta * @multiplier;

			PRINT 'checking next square with lat: '+convert(varchar(10),@lat)+', long: '+convert(varchar(10),@long)+', milesDelta: '+convert(varchar(10),@miles_delta)+', min: '+convert(varchar(10),@min)+', max: '+convert(varchar(10),@max);
			
			exec square_check @lat, @long, @miles_delta, @min, @max, @count = @currentCount OUTPUT;

		END;

		PRINT 'final count: ' + convert(varchar(10),@currentCount);

		SELECT @miles_delta * 2 AS 'square_size_in_miles', @currentCount AS 'doors_count' ;

END;