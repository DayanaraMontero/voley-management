CREATE OR REPLACE FUNCTION calcularRankingJugadoras()
    RETURNS TABLE
            (
                ID_JUGADORA INT,
                NOMBRE      TEXT,
                APELLIDOS   TEXT,
                PUNTOS      INT,
                RANK        INT
            )
AS
$$
BEGIN
    RETURN QUERY
        SELECT J.ID_JUGADORA,
               J.NOMBRE::text,
               J.APELLIDOS::text,
               COALESCE(SUM(E.BLOQUEOS + E.SAQUES + E.ATAQUES + E.DEFENSAS - E.ERRORES), 0)::int                      AS PUNTOS,
               DENSE_RANK()
               OVER (ORDER BY COALESCE(SUM(E.BLOQUEOS + E.SAQUES + E.ATAQUES + E.DEFENSAS - E.ERRORES),
                                       0) DESC)::int                                                                  AS RANK
        FROM JUGADORA J
                 LEFT JOIN
             ESTADISTICA E ON J.ID_JUGADORA = E.ID_JUGADORA
        GROUP BY J.ID_JUGADORA, J.NOMBRE, J.APELLIDOS
        ORDER BY RANK;
END;
$$ LANGUAGE plpgsql;



