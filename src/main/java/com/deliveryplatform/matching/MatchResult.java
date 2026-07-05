package com.deliveryplatform.matching;

import com.deliveryplatform.trips.Trip;

import java.math.BigDecimal;


/**
 * Résultat d'un matching trip/parcel viable.
 * L'instance n'existe que si le détour est dans la tolérance du trip —
 * pas de champ "viable" séparé à vérifier manuellement.
 */
public record MatchResult(
        Trip trip,
        BigDecimal price,
        double score, // plus bas = meilleur match
        boolean viable
) {}