package br.com.caelum.eats.pagamento;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "monolito")
public interface PedidoClienteComFeign {

    @PutMapping("/pedidos/{idDoPedido}/status")
    void notificaServicoDePedidoParaMudarStatus(@PathVariable("idDoPedido") Long id,
                                                @RequestBody MudancaDeStatusDoPedido body);

}

