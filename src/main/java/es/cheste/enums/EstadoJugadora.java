package es.cheste.enums;

/**
 * Este Enum representa los diferentes estados posibles de una jugadora
 * durante un partido.
 */
public enum EstadoJugadora {
    /**Estado que indica que la jugadora está participando en el partido.*/
    ACTIVA,

    /**Estado que indica que la jugadora ha sido sustituida y ya
     * no está en el partido.*/
    SUSTITUIDA,

    /**Estado que indica que la jugadora está lesionada y no
     *  puede continuar en el partido.*/
    LESIONADA,

    /**Estado que indica que la jugadora ha sido expulsada del partido.*/
    EXPULSADA,

    /**Estado que indica que la jugadora está en el campo desde el
     * inicio del partido.*/
    TITULAR,

    /**Estado que indica que la jugadora no está en el campo desde el inicio,
     * pero está disponible para entrar.*/
    RESERVA,

    /**Estado que indica que la jugadora no está participando en el partido.*/
    INACTIVA
}
