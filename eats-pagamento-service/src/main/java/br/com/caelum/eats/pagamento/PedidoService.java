package br.com.caelum.eats.pagamento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PedidoService {

	@Autowired
	private PedidoClienteComFeign pedidoCliente;
	
	@HystrixCommand(threadPoolKey = "detalhaThreadPool")
	PagamentoDto detalha(Long id, PagamentoRepository pagamentoRepo) {
		return pagamentoRepo.findById(id)
				.map(PagamentoDto::new)
				.orElseThrow(ResourceNotFoundException::new);
	}

	@HystrixCommand(fallbackMethod = "notificaServicoDePedidoParaMudarStatusFallback",
			threadPoolKey = "notificaServicoDePedidoParaMudarStatusThreadPool")
	PagamentoDto notificaServicoDePedidoParaMudarStatus(Long id, PagamentoRepository pagamentoRepo) {
		Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
		pagamento.setStatus(Pagamento.Status.CONFIRMADO);
		pedidoCliente.notificaServicoDePedidoParaMudarStatus(id, new MudancaDeStatusDoPedido("pago"));
		pagamentoRepo.save(pagamento);
		return new PagamentoDto(pagamento);
	}
	
	PagamentoDto notificaServicoDePedidoParaMudarStatusFallback(Long id, PagamentoRepository pagamentoRepo) {
		Pagamento pagamento = pagamentoRepo.findById(id).orElseThrow(ResourceNotFoundException::new);
		pagamento.setStatus(Pagamento.Status.PROCESSANDO);
		pagamentoRepo.save(pagamento);
		return new PagamentoDto(pagamento);
	}
	
}
