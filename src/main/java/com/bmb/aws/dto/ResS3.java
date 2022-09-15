/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmb.aws.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Optional;
import lombok.Data;

/**
 *
 * @author mares
 */
@Data
public class ResS3 {
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String file;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List files;
}
